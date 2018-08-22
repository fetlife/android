package com.bitlove.fetlife.inbound;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.legacy.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.bitlove.fetlife.BuildConfig;

//One of the inbound and extension points for OneSignal solution
//As of now other extension point is used but it is kept for logging purpuses and for future extensions
public class OneSignalBackgroundDataReceiver extends WakefulBroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        //keep this class empty for now for have the full call back mechanism for now
        //moving the processing code to the notification opener service seems to be sufficient now
        //but we will see after having more coverage of notifications
        Bundle dataBundle = intent.getBundleExtra("data");
        if (BuildConfig.DEBUG) {
            for (String key: dataBundle.keySet())
            {
                Log.d (getClass().getSimpleName(), key + " is a key in the bundle");
            }
        }
    }
}