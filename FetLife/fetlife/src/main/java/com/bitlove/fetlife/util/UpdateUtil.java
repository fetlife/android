package com.bitlove.fetlife.util;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.text.TextUtils;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateUtil {

//    public static void installApk(String url) {
//
//        try {
////            NotificationUtil.cancelNotification(context,NotificationUtil.RELEASE_NOTIFICATION_ID);
////            NotificationUtil.showProgressNotification(context, NotificationUtil.RELEASE_DOWNLOAD_NOTIFICATION_ID, context.getString(R.string.noification_title_downloading_apk), context.getString(R.string.noification_message_downloading_apk),0,0,null);
//
//            //get destination to update file and set Uri
//            //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
//            //application with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
//            //solution, please inform us in comment
//            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
//            String[] urlParts = url.split("/");
//            String fileName = urlParts[urlParts.length-1];
//            destination += fileName;
//            final Uri uri = Uri.parse("file://" + destination);
//
//            //Delete update file if exists
//            File file = new File(destination);
//            if (file.exists())
//                //file.delete() - test this, I think sometimes it doesnt work
//                file.delete();
//
//            //set downloadmanager
//            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//            request.setDescription(context.getString(R.string.notification_description_apk_download));
//            request.setTitle(context.getString(AppUtil.isVanilla(context) ? R.string.app_name_vanilla : R.string.app_name_kinky));
//
//            //set destination
//            request.setDestinationUri(uri);
//
//            // get download service and enqueue file
//            final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//            final long downloadId = manager.enqueue(request);
//
//            //set BroadcastReceiver to install app when .apk is downloaded
//            BroadcastReceiver onComplete = new BroadcastReceiver() {
//                public void onReceive(Context ctxt, Intent intent) {
//                    NotificationUtil.cancelNotification(context,NotificationUtil.RELEASE_DOWNLOAD_NOTIFICATION_ID);
//                    context.unregisterReceiver(this);
//
//                    Intent install = new Intent(Intent.ACTION_VIEW);
//                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    install.setDataAndType(uri,
//                            manager.getMimeTypeForDownloadedFile(downloadId));
//                    PendingIntent pendingIntent = PendingIntent.getActivity(context,0,install,0);
//
//                    NotificationUtil.showMessageNotification(context, NotificationUtil.RELEASE_DOWNLOADED_NOTIFICATION_ID, context.getString(R.string.noification_title_ready_to_install_apk), context.getString(R.string.noification_message_ready_to_install_apk),pendingIntent);
//
////                finish();
//                }
//            };
//            //tryConnect receiver for when .apk download is compete
//            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//
//        } catch (Throwable e) {
//            Crashlytics.logException(e);
//
//            NotificationUtil.cancelNotification(context,NotificationUtil.RELEASE_DOWNLOAD_NOTIFICATION_ID);
//
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setData(Uri.parse(url));
//            context.startActivity(intent);
//        }
//
//    }

    public static void installApk(String versionName) {
        String fileIUrl = getFileUrlForVersion(versionName);
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        installIntent.setDataAndType(Uri.parse(fileIUrl), "application/vnd.android.package-archive");
    }

    private static DownloadTask downloadTask;

    public static void downloadApk(String url, String versionName) {
        String localFileIUrl = getFileUrlForVersion(versionName);
        synchronized (UpdateUtil.class) {
            if (downloadTask != null && versionName.equals(downloadTask.getVersionName())) {
                return;
            } else {
                if (downloadTask != null) {
                    downloadTask.cancel(true);
                }
                downloadTask = new DownloadTask(versionName);
                downloadTask.execute(url,localFileIUrl);
            }
        }
    }


    public static boolean isVersionDownloaded(String versionName) {
        File file = new File(getFileUrlForVersion(versionName));
        return file.exists();
    }

    private static String getFileUrlForVersion(String versionName) {
        if (TextUtils.isEmpty(versionName)) {
            return null;
        }

        if (versionName.startsWith("v")) {
            versionName = versionName.substring(1);
        }

        String fileUrl = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        fileUrl += "fetlife-";
        fileUrl += versionName;
        fileUrl += ".apk";
        return fileUrl;
    }

    public static class DownloadTask extends AsyncTask<String, Integer, String> {

        private final String versionName;

        //private PowerManager.WakeLock mWakeLock;

        public DownloadTask(/*Context context,*/ String versionName) {
//            this.context = context;
            this.versionName = versionName;
        }

        public String getVersionName() {
            return versionName;
        }

        @Override
        protected String doInBackground(String... input) {

            InputStream inputStream = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL downloadUrl = new URL(input[0]);
                String localUrl = input[1];

                connection = (HttpURLConnection) downloadUrl.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                inputStream = connection.getInputStream();
                output = new FileOutputStream(localUrl);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = inputStream.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        inputStream.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                    getClass().getName());
//            mWakeLock.acquire();
//            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
//            mProgressDialog.setIndeterminate(false);
//            mProgressDialog.setMax(100);
//            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
//            mWakeLock.release();
//            mProgressDialog.dismiss();
        }
    }
}
