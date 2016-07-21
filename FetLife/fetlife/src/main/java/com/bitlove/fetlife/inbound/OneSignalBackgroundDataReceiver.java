package com.bitlove.fetlife.inbound;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.notification.OneSignalNotification;

import org.json.JSONException;
import org.json.JSONObject;

public class OneSignalBackgroundDataReceiver extends WakefulBroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        //keep this class empty for now for have the full call back mechanism for now
        //moving the processing code to the notification opener service seems to be sufficient now
        //but we will see after having more coverage of notifications

//        try {
//            Bundle dataBundle = intent.getBundleExtra("data");
//            String message = dataBundle.getString("message");
//            JSONObject customJSON = new JSONObject(dataBundle.getString("custom"));
//
//            if (BuildConfig.DEBUG) {
//                Log.w(getClass().getSimpleName(), customJSON.toString());
//            }
//
//            FetLifeApplication fetLifeApplication = getFetLifeApplication(context);
//
//            OneSignalNotification oneSignalNotification = fetLifeApplication.getNotificationParser().parseNotification(message, customJSON);
//            //oneSignalNotification.handle(fetLifeApplication);
//
//        } catch (JSONException e) {
//            //no valid custom information; nothing to handle
//            if (BuildConfig.DEBUG) {
//                Log.w(getClass().getSimpleName(), e);
//            }
//        }

    }

//    private FetLifeApplication getFetLifeApplication(Context context) {
//        return (FetLifeApplication) context.getApplicationContext();
//    }
}