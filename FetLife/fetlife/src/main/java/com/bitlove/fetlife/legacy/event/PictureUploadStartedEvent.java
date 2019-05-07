package com.bitlove.fetlife.legacy.event;

import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;

public class PictureUploadStartedEvent extends ServiceCallStartedEvent {

    private String pictureId;

    public PictureUploadStartedEvent(String pictureId) {
        super(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE);
        this.pictureId = pictureId;
    }

    public String getPictureId() {
        return pictureId;
    }

}
