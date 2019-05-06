package com.bitlove.fetlife.nativeapp.view.screen.standalone;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity;

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
                onBackPressed();
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
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    public static class GeneralSettings extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getPreferenceManager().setSharedPreferencesName(userPreferenceName);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.general_preferences);

            FetLifeApplication fetLifeApplication = FetLifeApplication.getInstance();

            final Preference vanillaSwitchPreference = findPreference(getString(R.string.settings_key_general_vanilla));
            SharedPreferences globalPreferences = PreferenceManager.getDefaultSharedPreferences(fetLifeApplication);
            vanillaSwitchPreference.getEditor().putBoolean(getString(R.string.settings_key_general_vanilla),globalPreferences.getBoolean(fetLifeApplication.getString(R.string.settings_key_general_vanilla),false));

            vanillaSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    boolean vanilla = (boolean) value;
                    FetLifeApplication fetLifeApplication = FetLifeApplication.getInstance();
                    fetLifeApplication.getPackageManager().setComponentEnabledSetting(
                            new ComponentName(fetLifeApplication.getPackageName(), fetLifeApplication.getPackageName() + ".StartActivity_Kinky"),
                            vanilla ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED : PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    fetLifeApplication.getPackageManager().setComponentEnabledSetting(
                            new ComponentName(fetLifeApplication.getPackageName(), fetLifeApplication.getPackageName() + ".StartActivity_Vanilla"),
                            vanilla ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    SharedPreferences globalPreferences = PreferenceManager.getDefaultSharedPreferences(fetLifeApplication);
                    globalPreferences.edit().putBoolean(fetLifeApplication.getString(R.string.settings_key_general_vanilla),vanilla).apply();
                    return true;
                }
            });
        }
    }

    public static class NotificationSettings extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getPreferenceManager().setSharedPreferencesName(userPreferenceName);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Load the preferences from an XML resource
                addPreferencesFromResource(R.xml.notification_preferences_android_o);
            } else {
                addPreferencesFromResource(R.xml.notification_preferences);
            }
        }
    }

    public static class FeedSettings extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getPreferenceManager().setSharedPreferencesName(userPreferenceName);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.feed_preferences);
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
                                    FetLifeWebViewActivity.Companion.startLogin(FetLifeApplication.getInstance(), null);
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
        if (FeedSettings.class.getName().equals(fragmentName)) {
            return true;
        }
        if (GeneralSettings.class.getName().equals(fragmentName)) {
            return true;
        }
        return false;
    }

}
