package com.bitlove.fetlife.legacy.model.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.legacy.event.VideoChunkUploadCancelRequestEvent;

public class ServiceCallCancelReceiver extends BroadcastReceiver {

    private static final String EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID";
    private static final String EXTRA_VIDEO_UPLOAD_ID = "EXTRA_VIDEO_UPLOAD_ID";
    private static final String ACTION_SERVICECALL_CANCEL = "com.bitlove.fetlife.ServiceCallCancelReceiver.RECEIVE";

    public static PendingIntent createVideoCancelPendingIntent(Context context, int notificationId, String videoUploadId) {
        Intent intent = new Intent(context, ServiceCallCancelReceiver.class);
        intent.setAction(ACTION_SERVICECALL_CANCEL);
        intent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        intent.putExtra(EXTRA_VIDEO_UPLOAD_ID, videoUploadId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, 0);
        return pendingIntent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String videoUploadId = intent.getStringExtra(EXTRA_VIDEO_UPLOAD_ID);
        getFetLifeApplication(context).getEventBus().post(new VideoChunkUploadCancelRequestEvent(videoUploadId));
        Intent cancelIntent = FetLifeApiIntentService.getActionIntent(context, FetLifeApiIntentService.ACTION_CANCEL_UPLOAD_VIDEO_CHUNK, videoUploadId);
        context.startService(cancelIntent);
    }

    private FetLifeApplication getFetLifeApplication(Context context) {
        return (FetLifeApplication) context.getApplicationContext();
    }
}
