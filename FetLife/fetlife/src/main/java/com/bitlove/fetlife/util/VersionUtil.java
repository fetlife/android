package com.bitlove.fetlife.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.github.Release;
import com.crashlytics.android.Crashlytics;

import java.text.ParseException;
import java.util.Comparator;

public class VersionUtil {

    private static final String PREFIX_VERSION = "v";
    private static final String PREF_NOTIFIED_LATEST_RELEASE = "PREF_NOTIFIED_LATEST_RELEASE";
    private static final String PREF_NOTIFIED_LATEST_PRERELEASE = "PREF_NOTIFIED_LATEST_PRERELEASE";

    public static boolean toBeNotified(Release release, boolean forcedCheck) {

        //TODO: use DI here, once DI Framework is integrated
        Context context = FetLifeApplication.getInstance();

        if (release == null) {
            return false;
        }

        Boolean isPreRelease = release.isPrerelease();

        if (isPreRelease && !shouldNotifyAboutPreReleases()) {
            return false;
        }

        String releaseVersionName = release.getTag();
        if (releaseVersionName.startsWith(PREFIX_VERSION)) {
            releaseVersionName = releaseVersionName.substring(PREFIX_VERSION.length());
        }

        //TODO: use DI here, once DI Framework is integrated
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String relevantPreferenceKey = isPreRelease ? PREF_NOTIFIED_LATEST_PRERELEASE : PREF_NOTIFIED_LATEST_RELEASE;
        String lastVersionNotifiedAbout = appPreferences.getString(relevantPreferenceKey, null);
        appPreferences.edit().putString(relevantPreferenceKey, releaseVersionName).apply();

        try {
            SemanticVersion currentVersion = new SemanticVersion(getCurrentVersionName());
            SemanticVersion releaseVersion = new SemanticVersion(releaseVersionName);

            if (currentVersion.compareTo(releaseVersion) >= 0) {
                return false;
            }

            return forcedCheck
                    || lastVersionNotifiedAbout == null
                    || releaseVersion.compareTo(new SemanticVersion(lastVersionNotifiedAbout)) > 0;

        } catch (ParseException pe) {
            Crashlytics.logException(pe);
            return true;
        }
    }

    private static boolean shouldNotifyAboutPreReleases() {
        FetLifeApplication fetLifeApplication = FetLifeApplication.getInstance();
        SharedPreferences userPreferences = fetLifeApplication.getUserSessionManager().getActiveUserPreferences();
        return userPreferences.getBoolean(fetLifeApplication.getString(R.string.settings_key_notification_prerelease_enabled), Boolean.valueOf(fetLifeApplication.getString(R.string.settings_default_notification_prerelease_enabled)));
    }

    public static String getCurrentVersionName() {
        try {
            Context context = FetLifeApplication.getInstance();
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static int getCurrentVersionInt() {
        try {
            Context context = FetLifeApplication.getInstance();
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public static Comparator<Release> getReleaseComparator() {
        return new Comparator<Release>() {
            @Override
            public int compare(Release release1, Release release2) {
                String versionName1 = release1.getTag();
                String versionName2 = release2.getTag();
                if (versionName1.startsWith(PREFIX_VERSION)) {
                    versionName1 = versionName1.substring(PREFIX_VERSION.length());
                }
                if (versionName2.startsWith(PREFIX_VERSION)) {
                    versionName2 = versionName2.substring(PREFIX_VERSION.length());
                }
                try {
                    return new SemanticVersion(versionName1).compareTo(new SemanticVersion(versionName2));
                } catch (ParseException pe) {
                    return 0;
                }
            }
        };
    }
}
