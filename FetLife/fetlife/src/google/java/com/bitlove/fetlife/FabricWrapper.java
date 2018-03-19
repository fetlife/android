package com.bitlove.fetlife;


import android.app.Application;
import io.fabric.sdk.android.Fabric;

public class FabricWrapper {

    public static void with(Application a, CrashlyticsWrapper crashlyticsWrapper){
        Fabric.with(a, crashlyticsWrapper.crashlytics);
    }

}