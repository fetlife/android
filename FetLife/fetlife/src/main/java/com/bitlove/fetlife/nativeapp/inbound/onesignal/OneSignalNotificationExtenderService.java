package com.bitlove.fetlife.nativeapp.inbound.onesignal;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.nativeapp.event.NotificationReceivedEvent;
import com.bitlove.fetlife.nativeapp.inbound.onesignal.notification.AnonymNotification;
import com.bitlove.fetlife.nativeapp.inbound.onesignal.notification.OneSignalNotification;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.util.AppUtil;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONObject;

/**
 * Extension point for One Signal notification library to override default display and onclick behaviour
 */
public class OneSignalNotificationExtenderService extends NotificationExtenderService {

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {

        logTime(notification);

        FetLifeApplication fetLifeApplication = getFetLifeApplication();

        //Parse the incoming notification so we can handle it accordingly based on its type
        NotificationParser notificationParser = fetLifeApplication.getNotificationParser();
        OneSignalNotification oneSignalNotification = notificationParser.parseNotification(fetLifeApplication, notification);

        //Look up for latest notification count and update the badge
        FetLifeApiIntentService.startApiCall(fetLifeApplication, FetLifeApiIntentService.ACTION_APICALL_NOTIFICATION_COUNTS);

        //Handle the incoming notification to do what is needed at the state of onreceived.
        boolean handledInternally = oneSignalNotification.handle(fetLifeApplication);

        //Check if the Notification was not fully handled internally and if it is not disabled by the user settings
        if (!handledInternally && oneSignalNotification.isEnabled(fetLifeApplication)) {
            //Check if the user use settings for hiding details of the notifications
            if (AppUtil.useAnonymNotifications(fetLifeApplication)) {
                AnonymNotification anonymNotification = new AnonymNotification();
                anonymNotification.display(getFetLifeApplication());
            } else {
                oneSignalNotification.display(fetLifeApplication);
            }
        }

        fetLifeApplication.getEventBus().post(new NotificationReceivedEvent());

        return true;
    }

    //Method to log time spent till the notifications was sent by the server
    private void logTime(OSNotificationReceivedResult notification) {
        try {
            long clientTime = System.currentTimeMillis();
            double serverTimeDouble = notification.payload.additionalData.getDouble("sent_at");
            long serverTime = (long)(serverTimeDouble*1000);
            String type = notification.payload.additionalData.getString("type");
            long googleTime = new JSONObject(notification.payload.rawPayload).getLong("google.sent_time");
            long time2Google = googleTime-serverTime;
            long time2Client = clientTime-googleTime;
            long totalTime = clientTime-serverTime;

            if (time2Google < 0 || time2Client < 0) {
                Crashlytics.logException(new Exception("Invalid notification track time"));
            } else {
                Answers.getInstance().logCustom(
                        new CustomEvent("CloudMessageReceived")
                                .putCustomAttribute("notificationId",notification.payload.notificationID)
                                .putCustomAttribute("type",type)
                                .putCustomAttribute("time2Google",time2Google)
                                .putCustomAttribute("time2Client",time2Client)
                                .putCustomAttribute("totalTime",totalTime));
            }

        } catch (Throwable t) {
            Crashlytics.logException(t);
        }
    }

    private FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplicationContext();
    }

}
