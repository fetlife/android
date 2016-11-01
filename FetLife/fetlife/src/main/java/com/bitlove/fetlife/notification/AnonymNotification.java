package com.bitlove.fetlife.notification;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.view.activity.ConversationsActivity;

public class AnonymNotification {

    public void display(FetLifeApplication fetLifeApplication) {

        Intent contentIntent = ConversationsActivity.createIntent(fetLifeApplication);

        PendingIntent contentPendingIntent =
                PendingIntent.getActivity(
                        fetLifeApplication,
                        0,
                        contentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(fetLifeApplication)
                        .setLargeIcon(BitmapFactory.decodeResource(fetLifeApplication.getResources(),R.mipmap.ic_launcher))
                        .setSmallIcon(R.drawable.ic_anonym_notif_small)
                        .setContentTitle(fetLifeApplication.getString(R.string.noification_title_new_one_or_more_notification))
                        .setContentText(fetLifeApplication.getString(R.string.noification_text_new_one_or_more_notification))
                        .setAutoCancel(true)
                        .setVibrate(new long[] {1000, 1000})
                        .setGroup(getClass().getSimpleName())
                        .setContentIntent(contentPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 0;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(fetLifeApplication);
        notificationManager.notify(mNotificationId, mBuilder.build());
    }



}
