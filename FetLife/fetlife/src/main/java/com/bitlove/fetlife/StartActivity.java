package com.bitlove.fetlife;

import android.app.Activity;
import android.os.Bundle;

import com.bitlove.fetlife.nativeapp.inbound.onesignal.notification.OneSignalNotification;
import com.bitlove.fetlife.nativeapp.session.UserSessionManager;
import com.bitlove.fetlife.nativeapp.view.screen.resource.FeedActivity;
import com.bitlove.fetlife.webapp.navigation.WebAppNavigation;
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity;

/**
 * Default Start Activity to make Activity title and App name independent
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignalNotification.Companion.clearNotifications(null, null);

        UserSessionManager userSessionManager = getFetLifeApplication().getUserSessionManager();
        if (userSessionManager.getCurrentUser() == null) {
            FetLifeWebViewActivity.Companion.startLogin(getFetLifeApplication(), null);
        } else {
            if (getFetLifeApplication().getUserSessionManager().getActiveUserPreferences().getBoolean(getString(R.string.settings_key_general_feed_as_start), false)) {
                FeedActivity.startActivity(this);
            } else {
                FetLifeWebViewActivity.Companion.startActivity(this, WebAppNavigation.WEBAPP_BASE_URL + "/inbox", true, R.id.navigation_bottom_inbox, false, null, null);
            }
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    protected FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplication();
    }

}
