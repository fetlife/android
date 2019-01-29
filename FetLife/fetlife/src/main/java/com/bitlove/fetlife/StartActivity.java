package com.bitlove.fetlife;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.bitlove.fetlife.inbound.onesignal.notification.OneSignalNotification;
import com.bitlove.fetlife.session.UserSessionManager;
import com.bitlove.fetlife.view.screen.resource.ConversationsActivity;
import com.bitlove.fetlife.view.screen.resource.FeedActivity;
import com.bitlove.fetlife.view.screen.standalone.LoginActivity;

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
            LoginActivity.startLogin(getFetLifeApplication());
        } else {
            if (getFetLifeApplication().getUserSessionManager().getActiveUserPreferences().getBoolean(getString(R.string.settings_key_general_feed_as_start),false)) {
                FeedActivity.startActivity(this);
            } else {
                ConversationsActivity.startActivity(this, false);
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
