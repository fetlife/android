package com.bitlove.fetlife.legacy.event;

import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;

public class VideoUploadFailedEvent extends ServiceCallFailedEvent {

    private final boolean tooLarge;

    public VideoUploadFailedEvent(boolean tooLarge) {
        super(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_VIDEO, false);
        this.tooLarge = tooLarge;
    }

    public boolean isTooLarge() {
        return tooLarge;
    }

}
