package com.bitlove.fetlife.nativeapp.view.screen.component;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.event.AuthenticationFailedEvent;
import com.bitlove.fetlife.nativeapp.event.LatestReleaseEvent;
import com.bitlove.fetlife.nativeapp.event.PictureUploadFailedEvent;
import com.bitlove.fetlife.nativeapp.event.PictureUploadFinishedEvent;
import com.bitlove.fetlife.nativeapp.event.PictureUploadStartedEvent;
import com.bitlove.fetlife.nativeapp.event.ServiceCallCancelEvent;
import com.bitlove.fetlife.nativeapp.event.ServiceCallCancelRequestEvent;
import com.bitlove.fetlife.nativeapp.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.nativeapp.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.nativeapp.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.nativeapp.event.VideoChunkUploadCancelEvent;
import com.bitlove.fetlife.nativeapp.event.VideoChunkUploadCancelRequestEvent;
import com.bitlove.fetlife.nativeapp.event.VideoChunkUploadFailedEvent;
import com.bitlove.fetlife.nativeapp.event.VideoChunkUploadFinishedEvent;
import com.bitlove.fetlife.nativeapp.event.VideoChunkUploadStartedEvent;
import com.bitlove.fetlife.nativeapp.event.VideoUploadFailedEvent;
import com.bitlove.fetlife.nativeapp.inbound.onesignal.update.UpdateBroadcastReceiver;
import com.bitlove.fetlife.nativeapp.model.pojos.github.Release;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.model.service.ServiceCallCancelReceiver;
import com.bitlove.fetlife.nativeapp.util.NotificationUtil;
import com.bitlove.fetlife.nativeapp.util.VersionUtil;
import com.bitlove.fetlife.nativeapp.view.screen.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class EventDisplayHandler {

    private static Map<String, Integer> notificationIdMap = new HashMap<>();

    public void onAuthenticationFailed(BaseActivity baseActivity, AuthenticationFailedEvent authenticationFailedEvent) {
        baseActivity.showToast(baseActivity.getString(R.string.error_authentication_failed));
    }

    public void onServiceCallFailed(BaseActivity baseActivity, ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent instanceof PictureUploadFailedEvent
                || FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE.equals(serviceCallFailedEvent.getServiceCallAction())) {
            baseActivity.showToast(baseActivity.getString(R.string.message_image_upload_failed));
            NotificationUtil.showMessageNotification(baseActivity.getFetLifeApplication(), NotificationUtil.PICTURE_UPLOAD_NOTIFICATION_ID++, baseActivity.getString(R.string.notification_picture_upload_title), baseActivity.getString(R.string.message_image_upload_failed), null);
        } else if (serviceCallFailedEvent instanceof VideoUploadFailedEvent) {
            baseActivity.showToast(baseActivity.getString(R.string.message_video_upload_failed_file_size, FetLifeApiIntentService.MAX_VIDEO_FILE_SIZE));
            NotificationUtil.showMessageNotification(baseActivity.getFetLifeApplication(), NotificationUtil.VIDEO_UPLOAD_NOTIFICATION_ID++, baseActivity.getString(R.string.notification_video_upload_title), baseActivity.getString(R.string.message_video_upload_failed_file_size, FetLifeApiIntentService.MAX_VIDEO_FILE_SIZE), null);
        } else if (serviceCallFailedEvent instanceof VideoChunkUploadFailedEvent) {
            VideoChunkUploadFailedEvent videoChunkUploadFailedEvent = (VideoChunkUploadFailedEvent) serviceCallFailedEvent;
            int notificationId = getNotificationIdFromMediaId(videoChunkUploadFailedEvent.getVideoId());
            dismissNotification(baseActivity, notificationId);
            String message = videoChunkUploadFailedEvent.isCancelled() ? baseActivity.getString(R.string.message_video_upload_cancelled) : baseActivity.getString(R.string.message_video_upload_failed);
            NotificationUtil.showMessageNotification(baseActivity.getFetLifeApplication(), notificationId, baseActivity.getString(R.string.notification_video_upload_title), message, null);
            baseActivity.showToast(message);
        } else if (FetLifeApiIntentService.ACTION_APICALL_UPLOAD_VIDEO.equals(serviceCallFailedEvent.getServiceCallAction())) {
            baseActivity.showToast(baseActivity.getString(R.string.message_video_upload_failed));
            NotificationUtil.showMessageNotification(baseActivity.getFetLifeApplication(), NotificationUtil.VIDEO_UPLOAD_NOTIFICATION_ID++, baseActivity.getString(R.string.notification_video_upload_title), baseActivity.getString(R.string.message_video_upload_failed), null);
        } else {
            if (serviceCallFailedEvent.isServerConnectionFailed()) {
                baseActivity.showToast(baseActivity.getResources().getString(R.string.error_connection_failed));
            } else {
                baseActivity.showToast(baseActivity.getResources().getString(R.string.error_apicall_failed));
            }
        }
    }

    public void onServiceCallFinished(BaseActivity baseActivity, ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent instanceof PictureUploadFinishedEvent) {
            int notificationId = getNotificationIdFromMediaId(((PictureUploadFinishedEvent) serviceCallFinishedEvent).getPictureId());
            dismissNotification(baseActivity, notificationId);
            NotificationUtil.showMessageNotification(baseActivity.getFetLifeApplication(), notificationId, baseActivity.getString(R.string.notification_picture_upload_title), baseActivity.getString(R.string.message_image_upload_finished), null);
            baseActivity.showToast(baseActivity.getString(R.string.message_image_upload_finished));
        } else if (serviceCallFinishedEvent instanceof VideoChunkUploadFinishedEvent) {
            VideoChunkUploadFinishedEvent videoChunkUploadFinishedEvent = (VideoChunkUploadFinishedEvent) serviceCallFinishedEvent;
            if (videoChunkUploadFinishedEvent.getChunk() == videoChunkUploadFinishedEvent.getChunkCount()) {
                int notificationId = getNotificationIdFromMediaId(((VideoChunkUploadFinishedEvent) serviceCallFinishedEvent).getVideoId());
                dismissNotification(baseActivity, notificationId);
                NotificationUtil.showMessageNotification(baseActivity.getFetLifeApplication(), notificationId, baseActivity.getString(R.string.notification_video_upload_title), baseActivity.getString(R.string.message_video_upload_finished), null);
                baseActivity.showToast(baseActivity.getString(R.string.message_video_upload_finished));
            }
        }
    }

    public void onServiceCallStarted(BaseActivity baseActivity, ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent instanceof PictureUploadStartedEvent) {
            notificationIdMap.put(((PictureUploadStartedEvent) serviceCallStartedEvent).getPictureId(), NotificationUtil.PICTURE_UPLOAD_NOTIFICATION_ID);
            NotificationUtil.showProgressNotification(baseActivity.getFetLifeApplication(), NotificationUtil.PICTURE_UPLOAD_NOTIFICATION_ID++, baseActivity.getString(R.string.notification_picture_upload_title), baseActivity.getString(R.string.notification_media_upload_text_inprogress), 0, 0, null);
            baseActivity.showToast(baseActivity.getString(R.string.message_image_upload_started));
        } else if (serviceCallStartedEvent instanceof VideoChunkUploadStartedEvent) {
            VideoChunkUploadStartedEvent videoChunkUploadStartedEvent = (VideoChunkUploadStartedEvent) serviceCallStartedEvent;
            if (videoChunkUploadStartedEvent.getChunk() == 1 && videoChunkUploadStartedEvent.getRetry() == 0) {
                notificationIdMap.put(videoChunkUploadStartedEvent.getVideoId(), NotificationUtil.VIDEO_UPLOAD_NOTIFICATION_ID);
                NotificationUtil.showProgressNotification(baseActivity.getFetLifeApplication(), NotificationUtil.VIDEO_UPLOAD_NOTIFICATION_ID, baseActivity.getString(R.string.notification_video_upload_title), baseActivity.getString(R.string.notification_media_upload_text_inprogress, 0, videoChunkUploadStartedEvent.getVideoSize()), 0, 0, ServiceCallCancelReceiver.createVideoCancelPendingIntent(baseActivity, NotificationUtil.VIDEO_UPLOAD_NOTIFICATION_ID, videoChunkUploadStartedEvent.getVideoId()));
                NotificationUtil.VIDEO_UPLOAD_NOTIFICATION_ID++;
                baseActivity.showToast(baseActivity.getString(R.string.message_video_upload_started));
            } else if (videoChunkUploadStartedEvent.getRetry() == 0) {
                int notificationId = getNotificationIdFromMediaId(videoChunkUploadStartedEvent.getVideoId());
                PendingIntent cancelIntent = videoChunkUploadStartedEvent.getChunk() * FetLifeApiIntentService.VIDEO_UPLOAD_CHUNK_SIZE_MBYTES >= videoChunkUploadStartedEvent.getVideoSize() ? null : ServiceCallCancelReceiver.createVideoCancelPendingIntent(baseActivity, notificationId, videoChunkUploadStartedEvent.getVideoId());
                NotificationUtil.showProgressNotification(baseActivity.getFetLifeApplication(), notificationId, baseActivity.getString(R.string.notification_video_upload_title), baseActivity.getString(R.string.notification_media_upload_text_inprogress, (videoChunkUploadStartedEvent.getChunk() - 1) * FetLifeApiIntentService.VIDEO_UPLOAD_CHUNK_SIZE_MBYTES, videoChunkUploadStartedEvent.getVideoSize()), 0, 0, cancelIntent);
            }
        }
    }

    public void onServiceCallCancelRequested(BaseActivity baseActivity, ServiceCallCancelRequestEvent serviceCallCancelRequestEvent) {
        if (serviceCallCancelRequestEvent instanceof VideoChunkUploadCancelRequestEvent) {
            VideoChunkUploadCancelRequestEvent videoChunkUploadCancelRequestEvent = (VideoChunkUploadCancelRequestEvent) serviceCallCancelRequestEvent;
            NotificationUtil.showProgressNotification(baseActivity.getFetLifeApplication(), getNotificationIdFromMediaId(videoChunkUploadCancelRequestEvent.getVideoId()), baseActivity.getString(R.string.notification_video_upload_title), baseActivity.getString(R.string.notification_media_upload_text_being_cancelled), 0, 0, null);
            baseActivity.showToast(baseActivity.getString(R.string.message_video_upload_being_cancelled));
        }
    }

    public void onServiceCallCancelProcessed(BaseActivity baseActivity, ServiceCallCancelEvent serviceCallCancelEvent) {
        if (serviceCallCancelEvent instanceof VideoChunkUploadCancelEvent) {
            VideoChunkUploadCancelEvent videoChunkUploadCancelEvent = (VideoChunkUploadCancelEvent) serviceCallCancelEvent;
            if (videoChunkUploadCancelEvent.isCancelSucceed()) {
                int notificationId = getNotificationIdFromMediaId(((VideoChunkUploadCancelEvent) serviceCallCancelEvent).getVideoId());
                dismissNotification(baseActivity, notificationId);
                NotificationUtil.showMessageNotification(baseActivity.getFetLifeApplication(), notificationId, baseActivity.getString(R.string.notification_video_upload_title), baseActivity.getString(R.string.message_video_upload_cancelled), null);
                baseActivity.showToast(baseActivity.getString(R.string.message_video_upload_cancelled));
            } else {
                baseActivity.showToast(baseActivity.getString(R.string.message_video_upload_cancel_failed));
            }
        }
    }

    public void onLatestReleaseChecked(BaseActivity baseActivity, LatestReleaseEvent latestReleaseEvent) {
        Release latestRelease = latestReleaseEvent.getLatestRelease();
        Release latestPreRelease = latestReleaseEvent.getLatestPreRelease();
        boolean forcedCheck = latestReleaseEvent.isForcedCheck();
        boolean notified = false;
        if (VersionUtil.toBeNotified(latestPreRelease, forcedCheck)) {
            notifyAboutNewRelease(baseActivity, latestPreRelease);
            notified = true;
        } else if (VersionUtil.toBeNotified(latestRelease, forcedCheck)) {
            notifyAboutNewRelease(baseActivity, latestRelease);
            notified = true;
        }
        if (forcedCheck && !notified) {
            baseActivity.showToast(baseActivity.getString(R.string.message_toast_no_update_found));
        }
    }

    private void notifyAboutNewRelease(BaseActivity baseActivity, Release release) {
        String header = baseActivity.getString(release.isPrerelease() ? R.string.notification_title_new_prerelease : R.string.notification_title_new_release);
        String message = baseActivity.getString(release.isPrerelease() ? R.string.notification_text_new_prerelease : R.string.notification_text_new_release, release.getTag());
        String url = release.getReleaseUrl();

//        NotificationHistoryItem notificationHistoryItem = new NotificationHistoryItem();
//        notificationHistoryItem.setTimeStamp(System.currentTimeMillis());
//        notificationHistoryItem.setDisplayHeader(header);
//        notificationHistoryItem.setDisplayMessage(message);
//        notificationHistoryItem.setLaunchUrl(UpdateNotification.getInnerLaunchUrl(url));
//        notificationHistoryItem.save();

        Intent notificationIntent = new Intent(baseActivity, UpdateBroadcastReceiver.class);
        notificationIntent.putExtra(UpdateBroadcastReceiver.EXTRA_URL, url);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(baseActivity, 0, notificationIntent, 0);
        NotificationUtil.showMessageNotification(baseActivity.getFetLifeApplication(), NotificationUtil.RELEASE_NOTIFICATION_ID, header, message, pendingIntent);

        baseActivity.showToast(baseActivity.getString(release.isPrerelease() ? R.string.notification_toast_new_prerelease : R.string.notification_toast_new_release));
    }

    private int getNotificationIdFromMediaId(String mediaId) {
        int notificationId = notificationIdMap.get(mediaId);
        return notificationId;
    }

    private void dismissNotification(BaseActivity baseActivity, int notificationId) {
        NotificationManager notifyManager = (NotificationManager) baseActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.cancel(notificationId);
    }
}
