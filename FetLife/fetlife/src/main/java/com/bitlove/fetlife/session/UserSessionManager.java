package com.bitlove.fetlife.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.bitlove.fetlife.model.pojos.User;
import com.bitlove.fetlife.util.PreferenceKeys;
import com.bitlove.fetlife.util.SecurityUtil;
import com.bitlove.fetlife.view.activity.SettingsActivity;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UserSessionManager {

    private static final String CONSTANT_ONESIGNAL_TAG_VERSION = "version";
    private static final String CONSTANT_ONESIGNAL_TAG_NICKNAME = "nickname";
    private static final String CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN = "member_token";

    private static final String APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED = "APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED";

    private final FetLifeApplication fetLifeApplication;

    private User currentUser;
    private SharedPreferences activePreferences;

    public UserSessionManager(FetLifeApplication fetLifeApplication) {
        this.fetLifeApplication = fetLifeApplication;
    }

    public void init() {
        String userKey = loadLastLoggedUserKey();
        if (userKey == null) {
            return;
        }

        applyVersionUpgradeIfNeeded(userKey);

        initUserPreferences(userKey);
        if (!getPasswordAlwaysPreference(userKey)) {
            loadUserDb(userKey);
            initDb();
            initUserPreferences(userKey);
            currentUser = readUserRecord();
        }
    }

    public synchronized void onUserLogIn(User loggedInUser, boolean passwordAlways) {
        if (!isSameUser(loggedInUser, currentUser)) {
            logOutUser(currentUser);
            logInUser(loggedInUser);
            currentUser = loggedInUser;
        } else {
            updateUserRecord(loggedInUser);
        }
        setPasswordAlwaysPreference(getUserKey(loggedInUser), passwordAlways);
    }

    public synchronized void onUserLogOut() {
        logOutUser(currentUser);
        currentUser = null;
    }

    public synchronized void onUserReset() {
        resetUser(currentUser);
        currentUser = null;
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }

    private synchronized void logInUser(User user) {
        String userKey = getUserKey(user);

        applyVersionUpgradeIfNeeded(userKey);

        saveLastLoggedUserKey(userKey);
        loadUserDb(getUserKey(user));
        initUserPreferences(userKey);
        initDb();
        updateUserRecord(user);
        registerToPushMessages(user);
    }

    private synchronized void logOutUser(User user) {
        closeDb();
        String userKey = user == null ? loadLastLoggedUserKey() : getUserKey(user);
        if (userKey != null) {
            saveUserDb(userKey);
        } else {
            clearDb();
        }
    }

    private synchronized void resetUser(User user) {
        String userKey = getUserKey(user);
        if (userKey == null) {
            return;
        }
        closeDb();
        deleteUserDb(userKey);
        removeLoggedUserKey(userKey);
        clearDb();
        clearUserPreferences(userKey);
        unregisterFromPushMessages(user);
    }

    private static String getUserKey(User user) {
        return user != null && user.getNickname() != null ? SecurityUtil.hash_sha256(user.getNickname()) : null;
    }

    private void registerToPushMessages(User user) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(CONSTANT_ONESIGNAL_TAG_VERSION,1);
            jsonObject.put(CONSTANT_ONESIGNAL_TAG_NICKNAME, user.getNickname());
            jsonObject.put(CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN, user.getNotificationToken());
            OneSignal.sendTags(jsonObject);
            OneSignal.setSubscription(true);
        } catch (JSONException e) {
            //TODO: think about possible error handling
        }
    }

    private void unregisterFromPushMessages(User user) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(CONSTANT_ONESIGNAL_TAG_VERSION, 1);
            jsonObject.put(CONSTANT_ONESIGNAL_TAG_NICKNAME, user.getNickname());
            jsonObject.put(CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN, "");
            OneSignal.sendTags(jsonObject);

            String[] tags = new String[]{
                    CONSTANT_ONESIGNAL_TAG_VERSION,
                    CONSTANT_ONESIGNAL_TAG_NICKNAME,
                    CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN
            };
            OneSignal.deleteTags(Arrays.asList(tags));
        } catch (JSONException e) {
            //TODO: think about possible error handling
        }
    }

    public SharedPreferences getActiveUserPreferences() {
        return activePreferences;
    }

    public boolean getActivePasswordAlwaysPreference() {
        return activePreferences.getBoolean(PreferenceKeys.PREF_KEY_PASSWORD_ALWAYS, true);
    }

    public boolean getPasswordAlwaysPreference(String userKey) {
        return getUserPreferences(userKey).getBoolean(PreferenceKeys.PREF_KEY_PASSWORD_ALWAYS, true);
    }

    private void setPasswordAlwaysPreference(String userKey, boolean checked) {
        getUserPreferences(userKey).edit().putBoolean(PreferenceKeys.PREF_KEY_PASSWORD_ALWAYS, checked).apply();
    }

    private String loadLastLoggedUserKey() {
        List<String> userHistory = loadUserHistoryFromPreference();
        return userHistory.isEmpty() ? null : userHistory.get(userHistory.size()-1);
    }

    private void saveLastLoggedUserKey(String userKey) {
        List<String> loggedInUsers = loadUserHistoryFromPreference();
        int currentPosition = Collections.binarySearch(loggedInUsers, userKey);
        if (currentPosition >= 0) {
            loggedInUsers.remove(currentPosition);
        }
        loggedInUsers.add(userKey);
        saveUserHistoryToPreference(loggedInUsers);
    }

    private void removeLoggedUserKey(String userKey) {
        List<String> loggedInUsers = loadUserHistoryFromPreference();
        int currentPosition = Collections.binarySearch(loggedInUsers, userKey);
        if (currentPosition >= 0) {
            loggedInUsers.remove(currentPosition);
        }
        saveUserHistoryToPreference(loggedInUsers);
    }

    private List<String> loadUserHistoryFromPreference() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(fetLifeApplication);
        String userHistory = preferences.getString(PreferenceKeys.PREF_KEY_USER_HISTORY, "");
        return new ArrayList<>(Arrays.asList(userHistory.split("%")));
    }

    private void saveUserHistoryToPreference(List<String> userHistory) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(fetLifeApplication);
        StringBuilder stringBuilder = new StringBuilder();
        for (String loggedInUser : userHistory) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("%");
            }
            stringBuilder.append(loggedInUser);
        }
        preferences.edit().putString(PreferenceKeys.PREF_KEY_USER_HISTORY, stringBuilder.toString()).apply();
    }

    private void updateUserRecord(User user) {
        if (user != null) {
            user.save();
        }
    }

    private User readUserRecord() {
        return new Select().from(User.class).querySingle();
    }

    private void saveUserDb(String userKey) {
        File databaseFile = fetLifeApplication.getDatabasePath(getDefaultDatabaseName());
        if (databaseFile == null) {
            return;
        }
        File userDatabaseFile = new File(databaseFile.getParentFile(), getUserDatabaseName(userKey));
        databaseFile.renameTo(userDatabaseFile);
    }

    private void loadUserDb(String userKey) {
        File databaseFile = fetLifeApplication.getDatabasePath(getDefaultDatabaseName());
        if (databaseFile == null) {
            return;
        }
        File userDatabaseFile = new File(databaseFile.getParentFile(), getUserDatabaseName(userKey));
        userDatabaseFile.renameTo(databaseFile);
    }

    private void initUserPreferences(String userKey) {
        String userPreferenceName = getUserPreferenceName(userKey);
        SettingsActivity.init(userPreferenceName);
        PreferenceManager.setDefaultValues(fetLifeApplication, userPreferenceName, Context.MODE_PRIVATE, R.xml.notification_preferences, false);
        activePreferences = getUserPreferences(userKey);
    }

    private SharedPreferences getUserPreferences(String userKey) {
        return fetLifeApplication.getSharedPreferences(getUserPreferenceName(userKey),Context.MODE_PRIVATE);
    }

    private void clearUserPreferences(String userKey) {
        getUserPreferences(userKey).edit().clear().apply();
    }

    private void deleteUserDb(String userKey) {
        File databaseFile = fetLifeApplication.getDatabasePath(getDefaultDatabaseName());
        if (databaseFile == null) {
            return;
        }
        File userDatabaseFile = new File(databaseFile.getParentFile(), getUserDatabaseName(userKey));
        userDatabaseFile.delete();
    }

    private void initDb() {
        FlowManager.init(new FlowConfig.Builder(fetLifeApplication).build());
    }

    private void closeDb() {
        FlowManager.destroy();
    }

    private void clearDb() {
        fetLifeApplication.deleteDatabase(getDefaultDatabaseName());
        fetLifeApplication.openOrCreateDatabase(getDefaultDatabaseName(), Context.MODE_PRIVATE, null);
    }

    private static String getDefaultDatabaseName() {
        //DBFlow library uses .db suffix, but they mentioned they might going to change this in the future
        return FetLifeDatabase.NAME + ".db";
    }

    private static String getUserDatabaseName(String userKey) {
        return FetLifeDatabase.NAME + "_" + userKey + ".db";
    }

    private static String getUserPreferenceName(String userKey) {
        return "fetlife" + "_" + userKey + ".pref";
    }

    private static boolean isSameUser(User user1, User user2) {
        if (user1 == null || user2 == null || user1.getNickname() == null) {
            return false;
        }
        return user1.getNickname().equals(user2.getNickname());
    }

    private void applyVersionUpgradeIfNeeded(String userKey) {
        SharedPreferences sharedPreferences = getUserPreferences(userKey);
        int lastVersionUpgrade = sharedPreferences.getInt(APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED, 0);
        if (lastVersionUpgrade < 10603) {
            SharedPreferences oldPreference = fetLifeApplication.getSharedPreferences(userKey, Context.MODE_PRIVATE);
            boolean oldSettings = oldPreference.getBoolean(PreferenceKeys.PREF_KEY_PASSWORD_ALWAYS,true);
            sharedPreferences.edit().putBoolean(PreferenceKeys.PREF_KEY_PASSWORD_ALWAYS, oldSettings).putInt(APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED, fetLifeApplication.getVersionNumber()).apply();
        }
    }

}
