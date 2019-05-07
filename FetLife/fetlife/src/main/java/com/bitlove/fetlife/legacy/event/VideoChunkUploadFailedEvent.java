package com.bitlove.fetlife.legacy.event;

import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;

public class VideoChunkUploadFailedEvent extends ServiceCallFailedEvent {

    private final int chunk;
    private final int chunkCount;
    private final boolean cancelled;
    private String videoId;

    public VideoChunkUploadFailedEvent(String videoId, int chunk, int chunkCount, boolean cancelled) {
        super(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_VIDEO_CHUNK, false);
        this.videoId = videoId;
        this.chunk = chunk;
        this.chunkCount = chunkCount;
        this.cancelled = cancelled;
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

    public boolean isCancelled() {
        return cancelled;
    }

}
