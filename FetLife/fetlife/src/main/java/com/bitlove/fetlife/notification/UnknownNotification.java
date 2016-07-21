package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;

import org.json.JSONObject;

public class UnknownNotification extends OneSignalNotification {

    public UnknownNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message,launchUrl,additionalData,id, group);
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        //Hide notification
        return true;
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
    }
}
