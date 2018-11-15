package com.bitlove.fetlife.notification;

import android.app.PendingIntent;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.db.NotificationHistoryItem;
import com.bitlove.fetlife.model.pojos.fetlife.db.NotificationHistoryItem_Table;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class InfoNotification extends OneSignalNotification {

    private static List<InfoNotification> notifications = new ArrayList<InfoNotification>();

    protected String collapseId;

    public InfoNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(id, title, message,launchUrl, group, additionalData);
        collapseId = additionalData != null ? additionalData.optString(NotificationParser.JSON_FIELD_STRING_COLLAPSE_ID) : null;
        notificationType = BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE;
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        return false;
    }

    public static void clearNotifications() {
        synchronized (notifications) {
            notifications.clear();
        }
    }

    @Override
    public void display(FetLifeApplication fetLifeApplication) {
        synchronized (notifications) {
            notifications.add(this);

            NotificationCompat.Builder notificationBuilder = getDefaultNotificationBuilder(fetLifeApplication);

            notificationBuilder.setContentTitle(title)
                    .setContentText(message);

            int notificationId = ++OneSignalNotification.NOTIFICATION_ID_INFO_INTERVAL;

            // Sets an ID for the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(fetLifeApplication);
            notificationManager.notify(notificationId, notificationBuilder.build());

            onNotificationDisplayed(fetLifeApplication, notificationId);
        }
    }

    @Override
    PendingIntent getPendingIntent(Context context) {
        return getLaunchPendingIntent(context, launchUrl, getNotificationType());
    }

    @Override
    public void onNotificationDisplayed(FetLifeApplication fetLifeApplication, int notificationId) {
        NotificationHistoryItem notificationHistoryItem = createNotificationItem(notificationId, collapseId);
        if (collapseId != null && collapseId.trim().length() != 0) {
            NotificationHistoryItem toBeCollapsedNotification = new Select().from(NotificationHistoryItem.class).where(NotificationHistoryItem_Table.collapseId.eq(notificationHistoryItem.getCollapseId())).querySingle();
            if (toBeCollapsedNotification != null) {
                OneSignal.cancelNotification(toBeCollapsedNotification.getDisplayId());
                toBeCollapsedNotification.delete();
            }
        }
        notificationHistoryItem.save();
    }

    @Override
    @Deprecated
    public void onClick(FetLifeApplication fetLifeApplication) {
//        if (launchUrl != null && launchUrl.trim().length() != 0) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setData(Uri.parse(launchUrl));
//            fetLifeApplication.startActivity(intent);
//        } else {
//            NotificationHistoryActivity.startActivity(fetLifeApplication, true);
//        }
    }

    @Override
    public String getAssociatedPreferenceKey(Context context) {
        return context.getString(R.string.settings_key_notification_info_enabled);
    }

}
