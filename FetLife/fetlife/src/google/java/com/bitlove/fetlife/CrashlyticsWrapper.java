package com.bitlove.fetlife;

import com.crashlytics.android.Crashlytics;


public class CrashlyticsWrapper {
    Crashlytics crashlytics;

    public static void logException(Throwable t) {
        Crashlytics.logException(t);
    }

    public static void log(String message) {
        Crashlytics.log(message);
    }

    public CrashlyticsWrapper() {
        this.crashlytics = new Crashlytics();
    }
}