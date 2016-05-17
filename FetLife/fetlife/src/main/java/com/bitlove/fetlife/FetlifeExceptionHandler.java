package com.bitlove.fetlife;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FetlifeExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String FILENAME_FETLIFE_LOG = "fetlife.log";

    private final FetLifeApplication fetLifeApplication;
    private Thread.UncaughtExceptionHandler defaultExceptionHandler;

    public FetlifeExceptionHandler(FetLifeApplication fetLifeApplication, Thread.UncaughtExceptionHandler defaultExceptionHandler) {
        this.fetLifeApplication = fetLifeApplication;
        this.defaultExceptionHandler = defaultExceptionHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        File externalStorageDir = Environment.getExternalStorageDirectory();
        File myFile = new File(externalStorageDir, FILENAME_FETLIFE_LOG);

        try {
            if (!myFile.exists()) {
                myFile.createNewFile();
            }

            Writer writer = new FileWriter(myFile, true);

            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));

            writer.write("\n");
            writer.write("*");
            writer.write("\n");
            writer.write(timeStamp);
            writer.write("\n");
            writer.write(errors.toString());
            writer.write("\n");

            writer.flush();
            writer.close();
        } catch (Exception e) {
        }

        defaultExceptionHandler.uncaughtException(thread, ex);
    }
}
