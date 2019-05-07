package com.bitlove.fetlife.legacy.event;

import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;

public class VideoChunkUploadFinishedEvent extends ServiceCallFinishedEvent {

    private final int chunk;
    private final int chunkCount;
    private String videoId;

    public VideoChunkUploadFinishedEvent(String videoId, int chunk, int chunkCount) {
        super(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_VIDEO_CHUNK, chunkCount);
        this.videoId = videoId;
        this.chunk = chunk;
        this.chunkCount = chunkCount;
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
}
