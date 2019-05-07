package com.bitlove.fetlife.legacy.inbound.onesignal.update;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bitlove.fetlife.common.app.FetLifeApplication;
import com.bitlove.fetlife.legacy.util.ApkUtil;
import com.crashlytics.android.Crashlytics;

public class UpdatePermissionActivity extends Activity {

    private static final String EXTRA_URL = "EXTRA_URL";

    private static final int PERMISSION_REQUEST_STORAGE = 101;
    private static final int PERMISSION_REQUEST_INSTALL = 142;

    public static void startActivity(Context context, String url ) {
        Intent intent = new Intent(context,UpdatePermissionActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestStoragePermission();
    }

    private boolean isStoragePermissionGranted() {
        return checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        if (isStoragePermissionGranted()) {
            requestInstallPermission();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestInstallPermission();
            } else {
                startBrowserApkInstall();
            }
        } else {
            Crashlytics.logException(new Exception("Unexpected execution"));
            finish();
        }
    }

    private boolean isInstallPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getPackageManager().canRequestPackageInstalls();
        } else {
            try {
                return Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS) == 1;
            } catch (Settings.SettingNotFoundException e) {
                Crashlytics.logException(e);
                return false;
            }
        }
    }

    private void requestInstallPermission() {
        if (isInstallPermissionGranted()) {
            startInternalApkInstall();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName())), PERMISSION_REQUEST_INSTALL);
            } else {
                startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_INSTALL) {
            if (isInstallPermissionGranted()) {
                startInternalApkInstall();
            } else {
                startBrowserApkInstall();
            }
        } else {
            Crashlytics.logException(new Exception("Unexpected execution"));
            finish();
        }
    }

    private void startInternalApkInstall() {
        String url = getIntent().getStringExtra(EXTRA_URL);
        ApkUtil.installApk(FetLifeApplication.getInstance(),url);
        finish();
    }

    private void startBrowserApkInstall() {
        String url = getIntent().getStringExtra(EXTRA_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        finish();
    }

}
