package com.bitlove.fetlife.nativeapp.event;

import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;

public class VideoChunkUploadCancelEvent extends ServiceCallCancelEvent {

    private final boolean cancelSucceed;
    private String videoId;

    public VideoChunkUploadCancelEvent(String videoId, boolean cancelSucceed) {
        super(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_VIDEO_CHUNK);
        this.videoId = videoId;
        this.cancelSucceed = cancelSucceed;
    }

    public String getVideoId() {
        return videoId;
    }

    public boolean isCancelSucceed() {
        return cancelSucceed;
    }
}
