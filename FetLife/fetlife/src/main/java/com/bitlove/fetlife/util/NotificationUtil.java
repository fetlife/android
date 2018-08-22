package com.bitlove.fetlife.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import androidx.core.app.NotificationCompat;

import com.bitlove.fetlife.FetLifeApplication;

public class NotificationUtil {

    public static int PICTURE_UPLOAD_NOTIFICATION_ID = 42;
    public static int VIDEO_UPLOAD_NOTIFICATION_ID = 1042;
    public static final int RELEASE_NOTIFICATION_ID = 10042;
    public static final int RELEASE_DOWNLOAD_NOTIFICATION_ID = 10142;
    public static final int RELEASE_DOWNLOADED_NOTIFICATION_ID = 10242;

    public static void cancelNotification(FetLifeApplication fetLifeApplication, int notificationId) {
        NotificationManager notifyManager = (NotificationManager) fetLifeApplication.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.cancel(notificationId);
    }

    public static void showMessageNotification(FetLifeApplication fetLifeApplication, int notificationId, String title, String text, PendingIntent pendingIntent) {
        NotificationManager notifyManager = (NotificationManager) fetLifeApplication.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(fetLifeApplication);

        builder.setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setSmallIcon(AppUtil.getAppIconResourceUrl(fetLifeApplication, true));
        notifyManager.notify(notificationId, builder.build());
    }

    public static void showProgressNotification(FetLifeApplication fetLifeApplication, int notificationId, String title, String text, int progress, int maxProgress, PendingIntent cancelIntent) {
        NotificationManager notifyManager = (NotificationManager) fetLifeApplication.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(fetLifeApplication);
        builder.setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(false)
                .setSmallIcon(AppUtil.getAppIconResourceUrl(fetLifeApplication, true));

        if (cancelIntent != null) {
            //TODO(VID): Add correct icon and text
            builder.addAction(android.R.drawable.ic_menu_delete, fetLifeApplication.getString(android.R.string.cancel), cancelIntent);
        }

        if (maxProgress > 0) {
            builder.setProgress(maxProgress, progress, false);
        } else {
            builder.setProgress(0, 0, true);
        }
        notifyManager.notify(notificationId, builder.build());
    }

}
