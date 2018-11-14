package com.bitlove.fetlife.inbound.onesignal;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.notification.OneSignalNotification;
import com.crashlytics.android.Crashlytics;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSNotificationPayload;
import com.onesignal.OneSignal;

import java.util.List;

/**
 * Inbound and extension point for OneSignal notification library to react on user interaction when user clicked on a notification
 */
@Deprecated
public class OnNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

    @Override
    public void notificationOpened(OSNotificationOpenResult osNotificationOpenResult) {
        if (osNotificationOpenResult == null || osNotificationOpenResult.notification == null) {
            Crashlytics.logException(new Exception("Null notification from one signal"));
            return;
        }
        if (!isValidNotification(osNotificationOpenResult)) {
            //Investigating further some strange exceptions at this point
            try {
                OneSignal.cancelNotification(osNotificationOpenResult.notification.androidNotificationId);
                Crashlytics.logException(new Exception("Invalid notification from one signal; Cancel invoked. json: " + osNotificationOpenResult.toJSONObject().toString()));
            } catch (Throwable t) {
                Crashlytics.logException(new Exception("Invalid notification from one signal; JSON not available; displayType: " + osNotificationOpenResult.notification.displayType));
            }
            return;
        }
        FetLifeApplication fetLifeApplication = FetLifeApplication.getInstance();

        OneSignalNotification oneSignalNotification = fetLifeApplication.getNotificationParser().parseNotification(fetLifeApplication, osNotificationOpenResult);
        oneSignalNotification.onClick(fetLifeApplication);
    }

    private boolean isValidNotification(OSNotificationOpenResult osNotificationOpenResult) {
        List<OSNotificationPayload> groupedNotifications = osNotificationOpenResult.notification.groupedNotifications;
        if (groupedNotifications == null || groupedNotifications.isEmpty()) {
            return osNotificationOpenResult.notification.payload != null;
        }
        return true;
    }

}
