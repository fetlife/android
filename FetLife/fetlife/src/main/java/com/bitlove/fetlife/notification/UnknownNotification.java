package com.bitlove.fetlife.notification;

import android.content.Context;

import com.bitlove.fetlife.FetLifeApplication;

import org.json.JSONObject;

@Deprecated
public class UnknownNotification extends OneSignalNotification {

    public UnknownNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(id, title, message,launchUrl, group, additionalData);
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        //Hide notification
        return true;
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
    }

    @Override
    public String getAssociatedPreferenceKey(Context context) {
        return null;
    }
}
