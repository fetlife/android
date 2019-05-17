package com.bitlove.fetlife.inbound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.NetworkUtil;
import com.crashlytics.android.Crashlytics;

/**
 * Application inbound point invoked by the Android OS in case of change of the Internet/network state
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String action = intent.getAction();
        if (!"android.net.conn.CONNECTIVITY_CHANGE".equalsIgnoreCase(action) && !"android.net.wifi.WIFI_STATE_CHANGED".equalsIgnoreCase(action)) {
            return;
        }

        int status = NetworkUtil.getConnectivityStatusString(context);
        if (status != NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            Crashlytics.log("NetworkStateChangeListener:network_changed:starting_pending_calls");
            //If internet is back, start services that might have some pending requests to be sent
            FetLifeApiIntentService.startPendingCalls(context, true);
        }
    }
}
