package com.bitlove.fetlife.view.activity.component;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.bitlove.fetlife.view.activity.BaseActivity;

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

}
