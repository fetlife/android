package com.bitlove.fetlife.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileOutputStream;

public class LogUtil {

    private static String localLog = "";

    public static void writeLog(String message) {

        String log = DateUtil.toServerString(System.currentTimeMillis()) + " - " + message + "\n";
        localLog += log;
        Toast.makeText(FetLifeApplication.getInstance(), log, Toast.LENGTH_SHORT).show();

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

        Toast.makeText(FetLifeApplication.getInstance(),"Your logs copied to your clipboard", Toast.LENGTH_LONG).show();
        
        return localLog;
    }

    public static void shareLogs(Context context) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "FetLife Logs");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, localLog);
        context.startActivity(Intent.createChooser(sharingIntent, "Share FetLife Logs"));
    }

}