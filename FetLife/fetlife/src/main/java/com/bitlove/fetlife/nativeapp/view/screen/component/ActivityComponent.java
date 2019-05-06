package com.bitlove.fetlife.nativeapp.view.screen.component;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.bitlove.fetlife.nativeapp.view.screen.BaseActivity;

public abstract class ActivityComponent {

    public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {

    }

    public void onActivityStarted(BaseActivity activity) {

    }

    public void onActivityResumed(BaseActivity activity) {

    }

    public void onActivityPaused(BaseActivity activity) {

    }

    public void onActivityStopped(BaseActivity activity) {

    }

    public void onActivitySaveInstanceState(BaseActivity activity, Bundle outState) {

    }

    public void onActivityDestroyed(BaseActivity activity) {

    }

    public Boolean onActivityCreateOptionsMenu(BaseActivity baseActivity, Menu menu) {
        return null;
    }

    public Boolean onActivityOptionsItemSelected(BaseActivity baseActivity, MenuItem item) {
        return null;
    }

    public Boolean onActivityNavigationItemSelected(BaseActivity baseActivity, MenuItem item) {
        return null;
    }

    public Boolean onActivityBackPressed(BaseActivity baseActivity){
        return null;
    }

    public Boolean onActivityKeyDown(BaseActivity baseActivity, int keyCode, KeyEvent e) {
        return null;
    }

    public void onActivityResult(BaseActivity activity, int resultCode, Intent data) {

    }

    public void onRequestPermissionsResult(BaseActivity baseActivity, int requestCode, String[] permissions, int[] grantResults) {

    }
}
