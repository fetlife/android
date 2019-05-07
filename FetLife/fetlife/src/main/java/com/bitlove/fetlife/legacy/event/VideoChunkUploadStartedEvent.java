package com.bitlove.fetlife.legacy.event;

import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;

public class VideoChunkUploadStartedEvent extends ServiceCallStartedEvent {

    private final int chunk;
    private final int chunkCount;
    private final int videoSize;
    private String videoId;
    private int retry;

    public VideoChunkUploadStartedEvent(String videoId, int chunk, int chunkCount, int videoSize, int retry) {
        super(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_VIDEO_CHUNK);
        this.videoId = videoId;
        this.chunk = chunk;
        this.chunkCount = chunkCount;
        this.videoSize = videoSize;
        this.retry = retry;
    }

    public String getVideoId() {
        return videoId;
    }

    public int getChunk() {
        return chunk;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public int getVideoSize() {
        return videoSize;
    }

    public int getRetry() {
        return retry;
    }
}
