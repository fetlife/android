package com.bitlove.fetlife.notification;

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

}
