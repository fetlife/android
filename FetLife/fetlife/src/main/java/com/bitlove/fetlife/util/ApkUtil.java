package com.bitlove.fetlife.util;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.crashlytics.android.Crashlytics;

import java.io.File;

public class ApkUtil {

    public static void installApk(final FetLifeApplication context, String url) {

        try {
            NotificationUtil.cancelNotification(context,NotificationUtil.RELEASE_NOTIFICATION_ID);
            NotificationUtil.showProgressNotification(context, NotificationUtil.RELEASE_DOWNLOAD_NOTIFICATION_ID, context.getString(R.string.noification_title_downloading_apk), context.getString(R.string.noification_message_downloading_apk),0,0,null);

            //get destination to update file and set Uri
            //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
            //application with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
            //solution, please inform us in comment
            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
            String[] urlParts = url.split("/");
            String fileName = urlParts[urlParts.length-1];
            destination += fileName;
            final Uri uri = Uri.parse("file://" + destination);

            //Delete update file if exists
            File file = new File(destination);
            if (file.exists())
                //file.delete() - test this, I think sometimes it doesnt work
                file.delete();

            //set downloadmanager
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription(context.getString(R.string.notification_description_apk_download));
            request.setTitle(context.getString(AppUtil.isVanilla(context) ? R.string.app_name_vanilla : R.string.app_name_kinky));

            //set destination
            request.setDestinationUri(uri);

            // get download service and enqueue file
            final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);

            //set BroadcastReceiver to install app when .apk is downloaded
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    NotificationUtil.cancelNotification(context,NotificationUtil.RELEASE_DOWNLOAD_NOTIFICATION_ID);
                    context.unregisterReceiver(this);

                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setDataAndType(uri,
                            manager.getMimeTypeForDownloadedFile(downloadId));
                    PendingIntent pendingIntent = PendingIntent.getActivity(context,0,install,0);

                    NotificationUtil.showMessageNotification(context, NotificationUtil.RELEASE_DOWNLOADED_NOTIFICATION_ID, context.getString(R.string.noification_title_ready_to_install_apk), context.getString(R.string.noification_message_ready_to_install_apk),pendingIntent);

//                finish();
                }
            };
            //tryConnect receiver for when .apk download is compete
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        } catch (Throwable e) {
            Crashlytics.logException(e);

            NotificationUtil.cancelNotification(context,NotificationUtil.RELEASE_DOWNLOAD_NOTIFICATION_ID);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        }

    }
}
