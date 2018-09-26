package com.bitlove.fetlife.inbound;

import android.os.Build;
import android.os.Debug;
import android.util.Log;

import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.event.NotificationReceivedEvent;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.notification.AnonymNotification;
import com.bitlove.fetlife.notification.NotificationParser;
import com.bitlove.fetlife.notification.OneSignalNotification;
import com.bitlove.fetlife.util.AppUtil;
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
/*TODO
    - revise texts

    - Test all 4 types of notifications

    - BackStack for messages Activity
    - Correct icon for notifications
    - Merge common code into super class
    - real setting array handling color etc

*/

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {

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

        FetLifeApplication fetLifeApplication = getFetLifeApplication();

        //Parse the incoming notification so we can handle it accordingly based on its type
        NotificationParser notificationParser = fetLifeApplication.getNotificationParser();
        OneSignalNotification oneSignalNotification = notificationParser.parseNotification(fetLifeApplication, notification);

        FetLifeApiIntentService.startApiCall(fetLifeApplication, FetLifeApiIntentService.ACTION_APICALL_NOTIFICATION_COUNTS);

        //Handle the incoming notification to do what is needed at the state of onreceived.
        boolean handledInternally = oneSignalNotification.handle(fetLifeApplication);

        //Check if the Notification was not fully handled internally and if it is not disabled by the user settings
        if (!handledInternally && oneSignalNotification.isEnabled(fetLifeApplication)) {
            //Check if the user use settings for hiding details of the notifications
            if (AppUtil.useAnonymNotifications(fetLifeApplication)) {
                AnonymNotification anonymNotification = new AnonymNotification(oneSignalNotification);
                anonymNotification.display(getFetLifeApplication());
            } else {
                oneSignalNotification.display(fetLifeApplication);
//            } else {
//                OverrideSettings overrideSettings = new OverrideSettings();
//                overrideSettings.extender = new NotificationCompat.Extender() {
//                    @Override
//                    public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
//                        // Sets the background notification color to Green on Android 5.0+ devices.
//                        builder.setSound(getFetLifeApplication().getUserSessionManager().getNotificationRingtone());
//                        builder.setVibrate(getFetLifeApplication().getUserSessionManager().getNotificationVibration());
//                        builder.setColor(getFetLifeApplication().getUserSessionManager().getNotificationColor());
//                        return builder.setVisibility(Notification.VISIBILITY_PRIVATE);
//                    }
//                };
//                OSNotificationDisplayedResult displayedResult  = displayNotification(overrideSettings);
//                //Let the notification react on notification displayed state so it can get the displayed notification identifier
//                oneSignalNotification.onNotificationDisplayed(fetLifeApplication, displayedResult.androidNotificationId);
            }
        }

        fetLifeApplication.getEventBus().post(new NotificationReceivedEvent());

        return true;
    }

    private FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplicationContext();
    }

}
