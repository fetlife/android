package com.bitlove.fetlife.common.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.database.sqlite.SQLiteException;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.structure.InvalidDBConfiguration;

public class FetLifeUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final long RESTART_DELAY = 300;

    private final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;
    private final PendingIntent restartIntent;

    FetLifeUncaughtExceptionHandler(Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler, PendingIntent restartIntent) {
        this.defaultUncaughtExceptionHandler = defaultUncaughtExceptionHandler;
        this.restartIntent = restartIntent;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (throwable instanceof SQLiteException ||
                throwable instanceof InvalidDBConfiguration ||
                throwable instanceof IllegalStateException) {
            if (FetLifeApplication.getInstance().getUserSessionManager().getCurrentUser() != null) {
                FetLifeApplication.getInstance().getUserSessionManager().resetAllUserDatabase();
            }
            Crashlytics.logException(new Exception("DB closed",throwable));
            //Duck exception DB is closed before background thread finished its job
            FetLifeApplication fetLifeApplication = FetLifeApplication.getInstance();
            if (fetLifeApplication.isAppInForeground()) {
                AlarmManager mgr = (AlarmManager) fetLifeApplication.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + RESTART_DELAY, restartIntent);
                System.exit(2);
            }
            System.exit(2);
        }

        defaultUncaughtExceptionHandler.uncaughtException(thread,throwable);
    }
}
