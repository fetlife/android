package com.bitlove.fetlife.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.util.PreferenceKeys;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    private static String userPreferenceName;

    public static void init(String userPreferenceName) {
        SettingsActivity.userPreferenceName = userPreferenceName;
    }

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    public static class NotificationSettings extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getPreferenceManager().setSharedPreferencesName(userPreferenceName);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.notification_preferences);
        }
    }

    public static class ProfileSettings extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getPreferenceManager().setSharedPreferencesName(userPreferenceName);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.profile_preferences);

            final Preference clearDataPreference = findPreference(getString(R.string.settings_key_profile_clear_data));
            clearDataPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity(), R.style.AppTheme_AlertDialog)
                            .setTitle(getString(R.string.title_delete_user_data_confirmation))
                            .setMessage(getString(R.string.message_delete_user_data_confirmation))
                            .setInverseBackgroundForced(true)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(getString(R.string.button_delete_user_data_confirmation), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    FetLifeApplication.getInstance().getUserSessionManager().onUserReset();
                                    LoginActivity.startLogin(FetLifeApplication.getInstance());
                                }
                            })
                            .setNegativeButton(getString(R.string.button_delete_user_data_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    return true;
                }
            });
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (NotificationSettings.class.getName().equals(fragmentName)) {
            return true;
        }
        if (ProfileSettings.class.getName().equals(fragmentName)) {
            return true;
        }
        return false;
    }
}
