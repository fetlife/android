package com.bitlove.fetlife.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.github.Release;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.crashlytics.android.Crashlytics;

public class VersionUtil {

    private static final String PREF_NOTIFIED_LATEST_RELEASE = "PREF_NOTIFIED_LATEST_RELEASE";
    private static final String PREF_NOTIFIED_LATEST_PRERELEASE = "PREF_NOTIFIED_LATEST_PRERELEASE";

    public static boolean toBeNotified(BaseActivity baseActivity, Release release, boolean forcedCheck) {

        if (release == null) {
            return false;
        }

        String releaseVersion = release.getTag();

        int version = getVersionInt(releaseVersion);

        if (version > 0 && version <= getCurrentVersionInt(baseActivity)) {
            return false;
        }

        if (release.isPrerelease() && !forcedCheck && !notifyAboutPrereleases(baseActivity)) {
            return false;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseActivity.getApplication());

        if (releaseVersion.equals(sharedPreferences.getString(release.isPrerelease() ? PREF_NOTIFIED_LATEST_PRERELEASE : PREF_NOTIFIED_LATEST_RELEASE, null))) {
            return forcedCheck;
        }
        sharedPreferences.edit().putString(release.isPrerelease() ? PREF_NOTIFIED_LATEST_PRERELEASE : PREF_NOTIFIED_LATEST_RELEASE, releaseVersion).apply();

        return true;
    }

    private static boolean notifyAboutPrereleases(BaseActivity baseActivity) {
        SharedPreferences sharedPreferences = baseActivity.getFetLifeApplication().getUserSessionManager().getActiveUserPreferences();
        return sharedPreferences.getBoolean(baseActivity.getString(R.string.settings_key_notification_prerelease_enabled), Boolean.valueOf(baseActivity.getString(R.string.settings_default_notification_prerelease_enabled)));
    }

    public static int getVersionInt(String versionText) {
        try {
            if (versionText.startsWith("v")) {
                versionText = versionText.substring(1);
            }
            String[] versionParts = versionText.split("\\.");
            versionText = versionParts[0];
            for (int i = 1; i < versionParts.length; i++) {
                if (versionParts[i].length() == 1) {
                    versionText += "0";
                }
                versionText += versionParts[i];
            }
            return Integer.parseInt(versionText);
        } catch (Exception e) {
            Crashlytics.logException(new Exception("Incorrect version text: " + versionText, e));
            return -1;
        }
    }

    public static int getCurrentVersionInt(Context context) {
        try {
            if (context == null) {
                return -1;
            }
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }
}
