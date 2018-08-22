package com.bitlove.fetlife.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.db.NotificationHistoryItem;
import com.bitlove.fetlife.util.AppUtil;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.TurboLinksViewActivity;
import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public abstract class OneSignalNotification {

    protected static final int NOTIFICATION_ID_DO_NOT_COLLAPSE = -1;

    public static final int NOTIFICATION_ID_ANONYM = 10;
    public static final int NOTIFICATION_ID_FRIEND_REQUEST = 20;
    public static final int NOTIFICATION_ID_MESSAGE = 30;
    public static final int NOTIFICATION_ID_LOVE = 40;
    public static final int NOTIFICATION_ID_COMMENT = 50;
    public static final int NOTIFICATION_ID_MENTION = 60;
    public static final int NOTIFICATION_ID_GROUP = 70;
    public static int NOTIFICATION_ID_INFO_INTERVAL = 1000;

    public static final String LAUNCH_URL_PARAM_SEPARATOR = ":";
    public static final String LAUNCH_URL_PREFIX = "FetLifeApp://";

    protected static PendingIntent getLaunchPendingIntent(Context context, String launchUrl, String notificationType) {
        Intent contentIntent;

        if (launchUrl != null) {
            contentIntent = new Intent(Intent.ACTION_VIEW);
            contentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            contentIntent.setData(Uri.parse(launchUrl));
        } else {
            contentIntent = TurboLinksViewActivity.createIntent(context,"notifications",context.getString(R.string.title_activity_notifications),true);
            contentIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE,notificationType);
        }

        PendingIntent contentPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        contentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        return contentPendingIntent;
    }

    public static void handleInnerLaunchUrl(Context context,String launchUrl) {
        try {
            String baseString = launchUrl.substring(LAUNCH_URL_PREFIX.length());
            String className = baseString.substring(0,baseString.indexOf(LAUNCH_URL_PARAM_SEPARATOR));
            Method method = Class.forName(className).getMethod("handleInnerLaunchUrl", Context.class, String.class);
            method.invoke(null,context,launchUrl);
        } catch (Throwable t) {
            Crashlytics.logException(t);
        }
    }

    protected final String id;
    protected final String title;
    protected final String message;
    protected final String launchUrl;
    protected final String group;
    protected final JSONObject additionalData;

    protected String notificationType;

    public OneSignalNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        this.title = title;
        this.message = message;
        this.launchUrl = launchUrl;
        this.additionalData = additionalData;
        this.group = group;
        this.id = id;
        notificationType = null;
    }

    public void display(FetLifeApplication fetLifeApplication) {}

    PendingIntent getPendingIntent(Context context) {
        return null;
    }

    public abstract boolean handle(FetLifeApplication fetLifeApplication);

    public void onNotificationDisplayed(FetLifeApplication fetLifeApplication, int notificationId) {}

    public abstract void onClick(FetLifeApplication fetLifeApplication);

    public abstract String getAssociatedPreferenceKey(Context context);

    public boolean isEnabled(FetLifeApplication fetLifeApplication) {
        SharedPreferences sharedPreferences = fetLifeApplication.getUserSessionManager().getActiveUserPreferences();
        if (sharedPreferences == null) {
            return false;
        }
        return sharedPreferences.getBoolean(getAssociatedPreferenceKey(fetLifeApplication), true);
    }

    protected NotificationCompat.Builder getDefaultNotificationBuilder(FetLifeApplication fetLifeApplication) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(fetLifeApplication)
                .setLargeIcon(BitmapFactory.decodeResource(fetLifeApplication.getResources(), R.mipmap.app_icon_kinky))
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setAutoCancel(true)
                .setVisibility(AppUtil.useAnonymNotifications(FetLifeApplication.getInstance()) ? Notification.VISIBILITY_SECRET : Notification.VISIBILITY_PRIVATE)
                .setContentIntent(getPendingIntent(fetLifeApplication))
                .setLights(fetLifeApplication.getUserSessionManager().getNotificationColor(),1000,1000)
                .setSound(fetLifeApplication.getUserSessionManager().getNotificationRingtone());

        long[] vibrationSetting = fetLifeApplication.getUserSessionManager().getNotificationVibration();
        if (vibrationSetting == null) {
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        } else {
            notificationBuilder.setVibrate(vibrationSetting);
        }

        return notificationBuilder;
    }

    protected String getNotificationType() {
        return notificationType;
    }

    protected void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    protected NotificationHistoryItem createNotificationItem(int notificationId, String collapseId) {
        NotificationHistoryItem notificationItem = new NotificationHistoryItem();
        try {
            double serverTimeDouble = additionalData.getDouble("sent_at");
            long serverTime = (long)(serverTimeDouble*1000);
            notificationItem.setTimeStamp(serverTime);
        } catch (JSONException t) {
            //skip, server change
        }
        notificationItem.setDisplayId(notificationId);
        notificationItem.setDisplayHeader(title);
        notificationItem.setDisplayMessage(message);
        notificationItem.setLaunchUrl(launchUrl);
        notificationItem.setCollapseId(collapseId);
        return notificationItem;
    }

}
