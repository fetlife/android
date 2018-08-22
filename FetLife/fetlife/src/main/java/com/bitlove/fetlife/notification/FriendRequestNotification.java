package com.bitlove.fetlife.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.FriendRequestsActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestNotification extends OneSignalNotification {

    private static List<FriendRequestNotification> notifications = new ArrayList<FriendRequestNotification>();

    public FriendRequestNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message,launchUrl,additionalData,id,group);
        notificationType = NotificationParser.JSON_VALUE_TYPE_FRIEND_REQUEST;
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

            String title = notifications.size() == 1 ? fetLifeApplication.getString(R.string.noification_title_new_friendrequest) : fetLifeApplication.getString(R.string.noification_title_new_friendrequests,notifications.size());
            String firstMessage = notifications.get(0).message;

            notificationBuilder.setContentTitle(title).setContentText(firstMessage);

            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(title);
            //TODO: localization
            inboxStyle.setSummaryText("…");
            for (int i=0; i < notifications.size(); i++) {
                FriendRequestNotification friendRequestNotification = notifications.get(i);
                //TODO: localization
                inboxStyle.addLine(friendRequestNotification.title + " " + friendRequestNotification.message);
            }
            notificationBuilder.setStyle(inboxStyle);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(fetLifeApplication);
            notificationManager.notify(OneSignalNotification.NOTIFICATION_ID_FRIEND_REQUEST, notificationBuilder.build());
        }
    }

    @Override
    PendingIntent getPendingIntent(Context context) {
        Intent contentIntent = FriendRequestsActivity.createIntent(context,true);
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
//        FriendRequestsActivity.startActivity(fetLifeApplication, true);
    }

    @Override
    public String getAssociatedPreferenceKey(Context context) {
        return context.getString(R.string.settings_key_notification_friendrequests_enabled);
    }
}
