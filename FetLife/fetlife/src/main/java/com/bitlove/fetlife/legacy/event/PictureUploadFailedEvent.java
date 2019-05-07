package com.bitlove.fetlife.legacy.event;

import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;

public class PictureUploadFailedEvent extends ServiceCallFailedEvent {

    private String pictureId;

    public PictureUploadFailedEvent(String pictureId, boolean serverConnectionFailed) {
        super(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE, serverConnectionFailed);
        this.pictureId = pictureId;
    }

    public String getPictureId() {
        return pictureId;
    }

}
