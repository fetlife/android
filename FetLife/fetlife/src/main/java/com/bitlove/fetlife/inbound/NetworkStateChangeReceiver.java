package com.bitlove.fetlife.inbound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.NetworkUtil;

/**
 * Application inbound point invoked by the Android SO in case of the any change with the Internet/network state
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
        if(status != NetworkUtil.NETWORK_STATUS_NOT_CONNECTED){

            //If internet is back, start services that might have some pending requests to be sent
            //TODO add a marker to them or add all relevant service calls to a list so there is no need to add them one by one here in the future

            if (!FetLifeApiIntentService.isActionInProgress(FetLifeApiIntentService.ACTION_APICALL_SEND_MESSAGES)) {
                FetLifeApiIntentService.startApiCall(context, FetLifeApiIntentService.ACTION_APICALL_SEND_MESSAGES);
            }
            if (!FetLifeApiIntentService.isActionInProgress(FetLifeApiIntentService.ACTION_APICALL_SEND_FRIENDREQUESTS)) {
                FetLifeApiIntentService.startApiCall(context, FetLifeApiIntentService.ACTION_APICALL_SEND_FRIENDREQUESTS);
            }
        }
    }
}
