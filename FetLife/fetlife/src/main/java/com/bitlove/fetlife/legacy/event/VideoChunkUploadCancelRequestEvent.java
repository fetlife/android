package com.bitlove.fetlife.legacy.event;

import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;

public class VideoChunkUploadCancelRequestEvent extends ServiceCallCancelRequestEvent {

    private String videoId;

    public VideoChunkUploadCancelRequestEvent(String videoId) {
        super(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_VIDEO_CHUNK);
        this.videoId = videoId;
    }

    public String getVideoId() {
        return videoId;
    }
}
