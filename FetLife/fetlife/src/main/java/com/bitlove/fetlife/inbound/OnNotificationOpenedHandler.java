package com.bitlove.fetlife.inbound;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.notification.NotificationParser;
import com.bitlove.fetlife.notification.OneSignalNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class OnNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
        FetLifeApplication fetLifeApplication = FetLifeApplication.getInstance();
        if (additionalData == null) {
            return;
        }
        OneSignalNotification oneSignalNotification = fetLifeApplication.getNotificationParser().parseNotification(fetLifeApplication, additionalData.optString(NotificationParser.JSON_FIELD_STRING_TITLE), message, additionalData.optString(NotificationParser.JSON_FIELD_STRING_LAUNCHURL), additionalData, null, additionalData.optString(NotificationParser.JSON_FIELD_STRING_GROUP));
        oneSignalNotification.onClick(fetLifeApplication);
    }
}
