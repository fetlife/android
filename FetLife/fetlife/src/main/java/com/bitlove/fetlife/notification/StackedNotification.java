package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.view.ConversationsActivity;

import org.json.JSONObject;

public class StackedNotification extends OneSignalNotification {

    public StackedNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message, launchUrl, additionalData, id, group);
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        //Should not be invoked
        return false;
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
        ConversationsActivity.startActivity(fetLifeApplication);
    }
}
