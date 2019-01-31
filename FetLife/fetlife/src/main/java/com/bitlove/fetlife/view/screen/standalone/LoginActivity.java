package com.bitlove.fetlife.view.screen.standalone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.LoginFailedEvent;
import com.bitlove.fetlife.event.LoginFinishedEvent;
import com.bitlove.fetlife.event.LoginStartedEvent;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.PreferenceUtils;
import com.bitlove.fetlife.view.dialog.LockScreenConfirmationDialog;
import com.bitlove.fetlife.view.dialog.MessageDialog;
import com.bitlove.fetlife.view.screen.resource.ConversationsActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoginActivity extends Activity {

    private static final String EXTRA_OPTIONAL_TOAST = "EXTRA_OPTIONAL_TOAST";
    private static final int REQUEST_CODE_CREATE_LOCK = 532145321;

    private EditText mUserNameView;
    private EditText mPasswordView;
    private CheckBox passwordPreferenceCheckBox;
    private Button logonButton;
    private Button logonProgressFakeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        if (getIntent().hasExtra(EXTRA_OPTIONAL_TOAST)) {
            showToast(getIntent().getStringExtra(EXTRA_OPTIONAL_TOAST));
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getFetLifeApplication());

        TextView previewText = (TextView)findViewById(R.id.text_preview);
        if (previewText != null) {
            if (BuildConfig.PREVIEW) {
                RotateAnimation rotate= (RotateAnimation) AnimationUtils.loadAnimation(this,R.anim.preview_rotation);
                previewText.setAnimation(rotate);
                previewText.setVisibility(View.VISIBLE);
            } else {
                previewText.setVisibility(View.INVISIBLE);
            }
        }

        boolean vanilla = sharedPreferences.getBoolean(getFetLifeApplication().getString(R.string.settings_key_general_vanilla),false);
        if (vanilla) {
            TextView loginHeader = (TextView) findViewById(R.id.login_header);
            loginHeader.setText(getString(R.string.app_name_vanilla));
            TextView loginText = (TextView) findViewById(R.id.login_text);
            loginText.setText(getString(R.string.logon_title_vanilla));
        }

        int lastVersionNotification = sharedPreferences.getInt(PreferenceUtils.MAIN_PREF_KEY_LAST_VERSION_NOTIFICATION,0);
        if (lastVersionNotification < 20620) {
            if (lastVersionNotification != 0) {
                MessageDialog.show(this,getString(R.string.title_dialog_reset_settings),getString(R.string.message_dialog_reset_settings));
            }
            sharedPreferences.edit().putInt(PreferenceUtils.MAIN_PREF_KEY_LAST_VERSION_NOTIFICATION,getFetLifeApplication().getVersionNumber()).apply();
        }


        // Set up the login form.
        mUserNameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        passwordPreferenceCheckBox = (CheckBox) findViewById(R.id.logon_password_preference);

        logonButton = (Button) findViewById(R.id.log_on_button);
        logonButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        logonProgressFakeButton = (Button) findViewById(R.id.logging_progress_button);

        if (vanilla) {
            findViewById(R.id.link_text_sign_up).setVisibility(View.GONE);
            findViewById(R.id.link_text_forgot_password).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getFetLifeApplication().getEventBus().register(this);
        if (FetLifeApiIntentService.isActionInProgress(FetLifeApiIntentService.ACTION_APICALL_LOGON_USER)) {
            showProgress();
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getFetLifeApplication());
            String prefKey = getFetLifeApplication().getString(R.string.settings_key_lock_screen_asked);
            boolean lockScreenAsked = sharedPreferences.getBoolean(prefKey,false);
            if (!lockScreenAsked) {
                sharedPreferences.edit().putBoolean(prefKey,true).apply();
                new LockScreenConfirmationDialog().newInstance(this,REQUEST_CODE_CREATE_LOCK).show(getFragmentManager());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        getFetLifeApplication().getEventBus().unregister(this);
    }

    private void showProgress() {
        mUserNameView.setEnabled(false);
        mPasswordView.setEnabled(false);
        logonButton.setVisibility(View.GONE);
        logonProgressFakeButton.setVisibility(View.VISIBLE);
    }

    private void dismissProgress() {
        mUserNameView.setEnabled(true);
        mPasswordView.setEnabled(true);
        logonButton.setVisibility(View.VISIBLE);
        logonProgressFakeButton.setVisibility(View.GONE);
    }

    /**
     * Attempts to sign in or tryConnect the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress();
            FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_LOGON_USER, username, password, Boolean.toString(passwordPreferenceCheckBox.isChecked()));
        }
    }

    public static void startLogin(FetLifeApplication fetLifeApplication) {
        fetLifeApplication.startActivity(createIntent(fetLifeApplication,null));
    }

    public static Intent createIntent(FetLifeApplication fetLifeApplication, String toast) {
        Intent intent = new Intent(fetLifeApplication, LoginActivity.class);
        if (toast != null) {
            intent.putExtra(EXTRA_OPTIONAL_TOAST,toast);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginStarted(LoginStartedEvent loginStartedEvent) {
        showProgress();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginFinished(LoginFinishedEvent loginFinishedEvent) {
        //dismissProgress();
        apply_v1_5_pwd_decision();
        ConversationsActivity.startActivity(this, false);
        finish();
    }

    //TODO: remove in later versions
    private void apply_v1_5_pwd_decision() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getFetLifeApplication());
        preferences.edit().putBoolean("v1_5_pwd_decision_made",true).apply();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogonFailed(LoginFailedEvent loginFailedEvent) {
        //TODO: handle different errors
        dismissProgress();
        if (loginFailedEvent.isServerConnectionFailed()) {
            showToast(getResources().getString(R.string.error_connection_failed));
        } else {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
        }
        mPasswordView.requestFocus();
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onSignUp(View v) {
        openLink("https://fetlife.com/signup_step1");
    }

    public void onForgotLogin(View v) {
        openLink("https://fetlife.com/users/password/new");
    }

    private void openLink(String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    private FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplication();
    }

}

