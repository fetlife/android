package com.bitlove.fetlife.legacy.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.bitlove.fetlife.BuildConfig;
import com.crashlytics.android.Crashlytics;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static void copyFileContent(File source, File target) {
        if (BuildConfig.DEBUG) {
            Log.e("FileUtil","Copying File content");
        }

        InputStream in = null;
        OutputStream out = null;
        try {

            if (!source.canRead()) {
                if (BuildConfig.DEBUG) {
                    Log.e("FileUtil","Source File cannot be read");
                }
                Crashlytics.logException(new Exception("File copy cannot be performed, source cannot be read"));
                return;
            }

            if (!target.exists()) {
                if (BuildConfig.DEBUG) {
                    Log.d("FileUtil","Target File does not exist; creating");
                }
                target.createNewFile();
            }

            if (!target.canWrite()) {
                if (BuildConfig.DEBUG) {
                    Log.e("FileUtil","Target File cannot be written");
                }
                Crashlytics.logException(new Exception("File copy cannot be performed, target cannot be written"));
                return;
            }

            in = new FileInputStream(source);
            out = new FileOutputStream(target);

            byte[] buffer = new byte[1024];
            int read;
            int sumRead = 0;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                sumRead += read;
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            if (BuildConfig.DEBUG) {
                Log.d("FileUtil","File copy performed with " + sumRead + " bytes");
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e("FileUtil","File copy failed",e);
            }
            Crashlytics.logException(e);
        }
    }

    public static void clearContent(File file) {
        if (BuildConfig.DEBUG) {
            Log.d("FileUtil","Clearing File content with length: " + file.length());
        }

        try {

            if (!file.canRead()) {
                if (BuildConfig.DEBUG) {
                    Log.e("FileUtil","File cannot be read");
                }
                Crashlytics.logException(new Exception("File copy cannot be performed, source cannot be read"));
                return;
            }

            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();

        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e("FileUtil","File content clear failed",e);
            }
            Crashlytics.logException(e);
        }
    }

    public static String[] splitFile(Context context, Uri uri, String name, int chunkSize) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return splitFileV19(context, uri, name, chunkSize);
        } else {
            return splitFileLegacy(context, uri, name, chunkSize);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String[] splitFileV19(Context context, Uri uri, String name, int chunkSize) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();

        List<String> chunkUris = new ArrayList<>();
        int partCounter = 1;

        int sizeOfFiles = chunkSize;
        byte[] buffer = new byte[sizeOfFiles];

        File outputDir = context.getCacheDir();

        try (BufferedInputStream bis = new BufferedInputStream(
                contentResolver.openInputStream(uri))) {

            int tmp ;
            while ((tmp = bis.read(buffer)) > 0) {
                File newFile = File.createTempFile(name, "" + partCounter, outputDir);
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    out.write(buffer, 0, tmp);
                }
                chunkUris.add(Uri.fromFile(newFile).toString());
            }
        }
        return chunkUris.toArray(new String[chunkUris.size()]);
    }

    private static String[] splitFileLegacy(Context context, Uri uri, String name, int chunkSize) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();

        List<String> chunkUris = new ArrayList<>();
        int partCounter = 1;

        int sizeOfFiles = chunkSize;
        byte[] buffer = new byte[sizeOfFiles];

        File outputDir = context.getCacheDir();
        BufferedInputStream bis = new BufferedInputStream(
                contentResolver.openInputStream(uri));

        try {
            int tmp;
            while ((tmp = bis.read(buffer)) > 0) {
                File newFile = File.createTempFile(name, "" + partCounter, outputDir);
                FileOutputStream out = new FileOutputStream(newFile);
                try {
                    out.write(buffer, 0, tmp);
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                chunkUris.add(Uri.fromFile(newFile).toString());
            }
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
        return chunkUris.toArray(new String[chunkUris.size()]);
    }
}
