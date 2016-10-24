package com.bitlove.fetlife;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bitlove.fetlife.inbound.OnNotificationOpenedHandler;
import com.bitlove.fetlife.model.api.FetLifeService;
import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.bitlove.fetlife.model.inmemory.InMemoryStorage;
import com.bitlove.fetlife.model.resource.ImageLoader;
import com.bitlove.fetlife.notification.NotificationParser;
import com.bitlove.fetlife.session.UserSessionManager;
import com.bitlove.fetlife.view.activity.resource.ResourceListActivity;
import com.crashlytics.android.Crashlytics;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.config.FlowManager;

import io.fabric.sdk.android.Fabric;
import org.greenrobot.eventbus.EventBus;

/**
 * Main Application class. The lifecycle of the object of this class is the same as the App itself
 */
public class FetLifeApplication extends Application {

    /**
     * Preference key for version number for last upgrade was executed.
     * Upgrade for certain version might be executed to ensure backward compatibility
     */
    private static final String APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED = "APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED";

    /**
     * Logout delay in case of additional task started that is outside of the App (like photo App for taking a photo)
     * We do not want to log out the user right away in this case
     */
    private static final long WAITING_FOR_RESULT_LOGOUT_DELAY_MILLIS = 60 * 1000;

    //****
    //App singleton behaviour to make it accessible where dependency injection is not possible
    //****

    private static FetLifeApplication instance;

    public static FetLifeApplication getInstance() {
        return instance;
    }

    /**
     * App version info fields
     */
    private String versionText;
    private int versionNumber;

    /**
     * Currently displayed Activity if there is any
     */
    private Activity foregroundActivity;

    //****
    //Service objects
    //****

    private FetLifeService fetLifeService;
    private ImageLoader imageLoader;
    private NotificationParser notificationParser;
    private EventBus eventBus;
    private UserSessionManager userSessionManager;
    private InMemoryStorage inMemoryStorage;

    @Override
    public void onCreate() {
        super.onCreate();

        //Setup default instance and callbacks
        instance = this;

        //Setup App version info
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionText = pInfo.versionName;
            versionNumber = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionText = getString(R.string.text_unknown);
        }

        //Init crash logging
        Fabric.with(this, new Crashlytics());

        //Init push notifications
        OneSignal.startInit(this).setNotificationOpenedHandler(new OnNotificationOpenedHandler()).init();
        OneSignal.enableNotificationsWhenActive(true);

        //Register activity call back to keep track of currently displayed Activity
        registerActivityLifecycleCallbacks(new ForegroundActivityObserver());

        //Init user session manager
        userSessionManager = new UserSessionManager(this);

        //Apply version upgrade if needed to ensure backward compatibility
        //Note: this place is intentional as user session manager might need to be created but not initialised to do the proper upgrade
        applyVersionUpgradeIfNeeded();

        userSessionManager.init();

        //Init service members
        try {
            fetLifeService = new FetLifeService(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        imageLoader = new ImageLoader(this);
        notificationParser = new NotificationParser();
        eventBus = EventBus.getDefault();
        inMemoryStorage = new InMemoryStorage();
    }

    //****
    //Displaying toast messages
    //****

    public void showToast(final int resourceId) {
        showToast(getResources().getString(resourceId));
    }

    public void showToast(final String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    public void showLongToast(final int resourceId) {
        showLongToast(getResources().getString(resourceId));
    }

    public void showLongToast(final String text) {
        showToast(text, Toast.LENGTH_LONG);
    }

    private void showToast(final String text, int length) {
        Toast.makeText(FetLifeApplication.this, text, length).show();
    }

    //****
    //Getter and helper methos for App foreground state
    //****

    public Activity getForegroundActivity() {
        return foregroundActivity;
    }

    public synchronized void setForegroundActivity(Activity foregroundActivity) {
        synchronized (userSessionManager) {
            this.foregroundActivity = foregroundActivity;
        }
    }

    public boolean isAppInForeground() {
        synchronized (userSessionManager) {
            return foregroundActivity != null;
        }
    }

    //****
    //Getters for service classes
    //****

    public InMemoryStorage getInMemoryStorage() {
        return inMemoryStorage;
    }

    public UserSessionManager getUserSessionManager() {
        return userSessionManager;
    }

    public FetLifeService getFetLifeService() {
        return fetLifeService;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public NotificationParser getNotificationParser() {
        return notificationParser;
    }

    public EventBus getEventBus() {
        return eventBus;
    }


    //****
    //Getters for App version info
    //****

    public String getVersionText() {
        return versionText;
    }

    public int getVersionNumber() {
        return versionNumber;
    }


    //****
    //Version upgrade method to senure backward compatibility
    //****

    private void applyVersionUpgradeIfNeeded() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int lastVersionUpgrade = sharedPreferences.getInt(APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED, 0);
        if (lastVersionUpgrade < 10510) {
            sharedPreferences.edit().clear().apply();

            FlowManager.destroy();
            deleteDatabase(FetLifeDatabase.NAME + ".db");
            openOrCreateDatabase(FetLifeDatabase.NAME + ".db", Context.MODE_PRIVATE, null);

            sharedPreferences.edit().putInt(APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED, versionNumber).apply();
        }
    }


    //****
    //Version upgrade method to senure backward compatibility
    //****

    private class ForegroundActivityObserver implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            setForegroundActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            setForegroundActivity(null);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            boolean isWaitingForResult = isWaitingForResult(activity);
            //Check if the new Screen is already displayed so the App is still in the foreground
            //Check if the Activity is topped due to configuration change like device rotation
            //Check if we started an external task (like taking photo) for that we should wait and keep the user logged in
            if (!isAppInForeground() && !activity.isChangingConfigurations() && !isWaitingForResult) {
                //If none of the above cases happen to be true log out the user in case (s)he selected to be logged out always
                if (userSessionManager.getActivePasswordAlwaysPreference()) {
                    userSessionManager.onUserLogOut();
                }
            } else if(isWaitingForResult) {
                //If we are waiting for an external task to be finished, start a delayed log out
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (userSessionManager) {
                            //After delay happened make sure if the user is still need to be logged out
                            //Check if App is still displayed
                            //Check if the user is not already logged out
                            //Check if the user wants us to log her/him out in case of leaving the app
                            if (!isAppInForeground() && userSessionManager.getCurrentUser() != null && userSessionManager.getActivePasswordAlwaysPreference()) {
                                userSessionManager.onUserLogOut();
                            }
                        }
                    }
                }, WAITING_FOR_RESULT_LOGOUT_DELAY_MILLIS);
            }
        }

        private boolean isWaitingForResult(Activity activity) {
            if (activity instanceof ResourceListActivity) {
                return ((ResourceListActivity)activity).isWaitingForResult();
            }
            return false;
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }

}

