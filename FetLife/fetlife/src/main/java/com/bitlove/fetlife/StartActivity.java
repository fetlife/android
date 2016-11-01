package com.bitlove.fetlife;

import android.app.Activity;
import android.os.Bundle;

import com.bitlove.fetlife.view.activity.ConversationsActivity;
import com.bitlove.fetlife.view.activity.LoginActivity;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversationsActivity.startActivity(this);
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
