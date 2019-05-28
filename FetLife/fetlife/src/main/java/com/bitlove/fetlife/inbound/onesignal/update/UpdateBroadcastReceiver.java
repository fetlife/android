package com.bitlove.fetlife.inbound.onesignal.update;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.util.ApkUtil;

public class UpdateBroadcastReceiver extends BroadcastReceiver {

    public static final String EXTRA_URL = "EXTRA_URL";

    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getStringExtra(EXTRA_URL);
        if (url == null) {
            return;
        }
        UpdatePermissionActivity.startActivity(context,url);
    }
}
