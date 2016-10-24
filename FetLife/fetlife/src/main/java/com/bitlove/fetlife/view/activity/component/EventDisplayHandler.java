package com.bitlove.fetlife.view.activity.component;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.AuthenticationFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.activity.BaseActivity;

public class EventDisplayHandler {

    public void onAuthenticationFailed(BaseActivity baseActivity, AuthenticationFailedEvent authenticationFailedEvent) {
        baseActivity.showToast(baseActivity.getString(R.string.authentication_failed));
    }

    public void onServiceCallFailed(BaseActivity baseActivity, ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent.getServiceCallAction().equals(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE)) {
            baseActivity.showToast(baseActivity.getString(R.string.message_image_upload_failed));
        } else {
            if (serviceCallFailedEvent.isServerConnectionFailed()) {
                baseActivity.showToast(baseActivity.getResources().getString(R.string.error_connection_failed));
            } else {
                baseActivity.showToast(baseActivity.getResources().getString(R.string.error_apicall_failed));
            }
        }
    }

    public void onServiceCallFinished(BaseActivity baseActivity, ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction().equals(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE)) {
            baseActivity.showToast(baseActivity.getString(R.string.message_image_upload_finished));
        }
    }

    public void onServiceCallStarted(BaseActivity baseActivity, ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent.getServiceCallAction().equals(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE)) {
            baseActivity.showToast(baseActivity.getString(R.string.message_image_upload_started));
        }
    }

}
