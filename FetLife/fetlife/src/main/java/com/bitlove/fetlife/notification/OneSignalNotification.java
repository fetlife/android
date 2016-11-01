package com.bitlove.fetlife.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bitlove.fetlife.FetLifeApplication;

import org.json.JSONObject;

public abstract class OneSignalNotification {

    protected final String id;
    protected final String title;
    protected final String message;
    protected final String launchUrl;
    protected final String group;
    protected final JSONObject additionalData;

    public OneSignalNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        this.title = title;
        this.message = message;
        this.launchUrl = launchUrl;
        this.additionalData = additionalData;
        this.group = group;
        this.id = id;
    }

    public abstract boolean handle(FetLifeApplication fetLifeApplication);

    public void onNotificationDisplayed(FetLifeApplication fetLifeApplication, int notificationId) {}

    public abstract void onClick(FetLifeApplication fetLifeApplication);

    public abstract String getAssociatedPreferenceKey(Context context);

    public boolean isEnabled(FetLifeApplication fetLifeApplication) {
        SharedPreferences sharedPreferences = fetLifeApplication.getUserSessionManager().getActiveUserPreferences();
        return sharedPreferences.getBoolean(getAssociatedPreferenceKey(fetLifeApplication), true);
    }

}
