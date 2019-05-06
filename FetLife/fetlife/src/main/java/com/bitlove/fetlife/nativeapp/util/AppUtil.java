package com.bitlove.fetlife.nativeapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;

public class AppUtil {

    public static final int getAppIconResourceUrl(FetLifeApplication fetLifeApplication, boolean forNotification) {

        if (useAnonymNotifications(fetLifeApplication)) {
            return forNotification ? R.drawable.ic_anonym_notif_small : R.mipmap.app_icon_vanilla;
        } else {
            return forNotification ? R.drawable.ic_stat_onesignal_default : R.mipmap.app_icon_kinky;
        }
    }

    public static boolean isVanilla(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getBoolean(context.getApplicationContext().getString(R.string.settings_key_general_vanilla),false);
    }

    public static boolean useAnonymNotifications(FetLifeApplication fetLifeApplication) {
        SharedPreferences sharedPreferences = fetLifeApplication.getUserSessionManager().getActiveUserPreferences();
        return sharedPreferences.getBoolean(fetLifeApplication.getString(R.string.settings_key_notification_anonymtext),false);
    }
}
