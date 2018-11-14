package com.bitlove.fetlife.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.db.NotificationHistoryItem;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.TurboLinksViewActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoveNotification extends OneSignalNotification {

    private static List<LoveNotification> notifications = new ArrayList<LoveNotification>();

    public LoveNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(id, title, message,launchUrl, group, additionalData);
        notificationType = NotificationParser.JSON_VALUE_TYPE_CONVERSATION_RESPONSE;
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

            List<String> loves = getGroupedLovedTexts(fetLifeApplication, notifications);
            String title = fetLifeApplication.getString(R.string.noification_title_new_loves);
            String firstLove = loves.get(0);

            notificationBuilder.setContentTitle(title).setContentText(firstLove);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(title);
            //TODO: localization
            inboxStyle.setSummaryText("…");
            for (String message : loves) {
                inboxStyle.addLine(message);
            }
            notificationBuilder.setStyle(inboxStyle);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(fetLifeApplication);
            notificationManager.notify(OneSignalNotification.NOTIFICATION_ID_LOVE, notificationBuilder.build());

            onNotificationDisplayed(fetLifeApplication,NOTIFICATION_ID_DO_NOT_COLLAPSE);
        }
    }

    @Override
    public void onNotificationDisplayed(FetLifeApplication fetLifeApplication, int notificationId) {
        NotificationHistoryItem notificationHistoryItem = createNotificationItem(notificationId, null);
        notificationHistoryItem.save();
    }

    @Override
    PendingIntent getPendingIntent(Context context) {
        Intent contentIntent = TurboLinksViewActivity.createIntent(context,"notifications",context.getString(R.string.title_activity_notifications), true, null, true);
        contentIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE,getNotificationType());

        PendingIntent contentPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        contentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        return contentPendingIntent;
    }

    @Override
    @Deprecated
    public void onClick(FetLifeApplication fetLifeApplication) {

    }

    @Override
    public String getAssociatedPreferenceKey(Context context) {
        return context.getString(R.string.settings_key_notification_loves_enabled);
    }

    private List<String> getGroupedLovedTexts(FetLifeApplication fetLifeApplication, List<LoveNotification> notifications) {
        LinkedHashMap<String,Integer> userLoveGroups = new LinkedHashMap<>();
        for (LoveNotification notification : notifications) {
            Integer userLoveCount = userLoveGroups.get(notification.title);
            if (userLoveCount == null) {
                userLoveCount = 1;
            } else {
                userLoveCount++;
            }
            userLoveGroups.put(notification.title,userLoveCount);
        }
        List<String> loves = new ArrayList<>();
        for (Map.Entry<String,Integer> userLoveGroup : userLoveGroups.entrySet()) {
            //ignore count for now
            loves.add(fetLifeApplication.getString(R.string.noification_text_new_love,userLoveGroup.getKey()));
        }
        Collections.reverse(loves);
        return loves;
    }
}
