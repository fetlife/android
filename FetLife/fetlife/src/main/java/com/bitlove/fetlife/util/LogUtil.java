package com.bitlove.fetlife.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.FetLifeApplication;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileOutputStream;

public class LogUtil {

    private static String localLog = "";

    public static void writeLog(String message) {

        if (!BuildConfig.DEBUG) {
            return;
        }

        String log = DateUtil.toServerString(System.currentTimeMillis()) + " - " + message + "\n";
        localLog += log;

        try {
            File file = new File(FetLifeApplication.getInstance().getExternalFilesDir(null),"extra.log");
            if (!file.exists()) file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file,true);
            fos.write(log.getBytes());
            fos.close();
        } catch (Throwable t) {
            Crashlytics.logException(new Exception("Extra log exception"));
        }
    }

    public static String readLocalLog() {
        return localLog;
    }

    public static String copyLocalLogToClipBoard() {

        ClipboardManager clipboard = (ClipboardManager) FetLifeApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("FetLife Log", localLog);
        clipboard.setPrimaryClip(clip);
        
        return localLog;
    }

}