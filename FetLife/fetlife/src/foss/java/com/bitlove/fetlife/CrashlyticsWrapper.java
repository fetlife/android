package com.bitlove.fetlife;


import android.util.Log;

public class CrashlyticsWrapper {
    private static final String TAG = "Crashlytics";
    public static void logException(Throwable t){
        Log.d(TAG, t.toString());
    }

    public static void log(String message) {
        Log.d(TAG, message);
    }

}