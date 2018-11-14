package com.bitlove.fetlife.inbound.onesignal.update;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.notification.InfoNotification;
import com.bitlove.fetlife.util.ApkUtil;

import org.json.JSONObject;

public class UpdateNotification extends InfoNotification {

    private UpdateNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message, launchUrl, additionalData, id, group);
    }

    public static String getInnerLaunchUrl(String url) {
        return LAUNCH_URL_PREFIX + UpdateNotification.class.getName()+LAUNCH_URL_PARAM_SEPARATOR+url;
    }

    public static void handleInnerLaunchUrl(Context context, String launchUrl) {
        String url = launchUrl.substring((LAUNCH_URL_PREFIX+UpdateNotification.class.getName()+LAUNCH_URL_PARAM_SEPARATOR).length());
        if (context.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            ApkUtil.installApk(FetLifeApplication.getInstance(),url);
        } else {
            UpdatePermissionActivity.startActivity(context,url);
        }
    }


}
