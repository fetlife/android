package com.bitlove.fetlife.util;

import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.FetLifeApplication;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileOutputStream;

public class LogUtil {

    public static void writeLog(String message) {

        if (!BuildConfig.DEBUG) {
            return;
        }

        try {
            File file = new File(FetLifeApplication.getInstance().getExternalFilesDir(null),"extra.log");
            if (!file.exists()) file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file,true);
            String log = DateUtil.toServerString(System.currentTimeMillis()) + " - " + message + "\n";
            fos.write(log.getBytes());
            fos.close();
        } catch (Throwable t) {
            Crashlytics.logException(new Exception("Extra log exception"));
        }
    }
}