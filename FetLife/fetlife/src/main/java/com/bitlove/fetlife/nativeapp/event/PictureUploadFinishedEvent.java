package com.bitlove.fetlife.nativeapp.event;

import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;

public class PictureUploadFinishedEvent extends ServiceCallFinishedEvent {

    private String pictureId;

    public PictureUploadFinishedEvent(String pictureId) {
        super(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE, 1);
        this.pictureId = pictureId;
    }

    public String getPictureId() {
        return pictureId;
    }

}
