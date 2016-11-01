package com.bitlove.fetlife.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.AuthenticationFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.User;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.EmojiUtil;
import com.bitlove.fetlife.view.dialog.MediaUploadSelectionDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ResourceListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String PREFERENCE_VERSION_NOTIFICATION_INT = "PREFERENCE_VERSION_NOTIFICATION_INT";

    protected FloatingActionButton floatingActionButton;
    protected NavigationView navigationView;
    protected View navigationHeaderView;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager recyclerLayoutManager;
    protected View inputLayout;
    protected View inputIcon;
    protected EditText textInput;
    protected ProgressBar progressIndicator;

    private boolean waitingForResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        verifyUser();

        if (isFinishing()) {
            return;
        }

        //TODO: think of moving content stuff out of this class/method
        setContentView(R.layout.activity_resource);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        inputLayout = findViewById(R.id.text_input_layout);
        inputIcon = findViewById(R.id.text_send_icon);
        textInput = (EditText) findViewById(R.id.text_input);
        //textInput.setSingleLine(false);
        //textInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
//        textInput.setFilters(new InputFilter[]{new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                  //Custom Emoji Support will go here
//        }});

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressIndicator = (ProgressBar) findViewById(R.id.toolbar_progress_indicator);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationHeaderView = navigationView.getHeaderView(0);

        User currentUser = getFetLifeApplication().getUserSessionManager().getCurrentUser();
        if (currentUser != null) {
            TextView headerTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_text);
            headerTextView.setText(currentUser.getNickname());
            TextView headerSubTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_subtext);
            headerSubTextView.setText(currentUser.getMetaInfo());
            ImageView headerAvatar = (ImageView) navigationHeaderView.findViewById(R.id.nav_header_image);
            getFetLifeApplication().getImageLoader().loadImage(this, currentUser.getAvatarLink(), headerAvatar, R.drawable.dummy_avatar);
            final String selfLink = currentUser.getLink();
            if (selfLink != null) {
                headerAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(selfLink));
                        startActivity(intent);
                    }
                });
            }
        }

        showVersionSnackBarIfNeeded();
    }

    private void showVersionSnackBarIfNeeded() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int lastVersionNotification = preferences.getInt(PREFERENCE_VERSION_NOTIFICATION_INT, 0);
        int versionNumber = getFetLifeApplication().getVersionNumber();
        if (lastVersionNotification < versionNumber) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.snackbar_version_notification, getFetLifeApplication().getVersionText()), Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.color_accent_light));
            TextView snackText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackText.setTextColor(getResources().getColor(R.color.color_accent));
            snackText.setTypeface(null, Typeface.BOLD);
            snackbar.show();
            preferences.edit().putInt(PREFERENCE_VERSION_NOTIFICATION_INT, versionNumber).apply();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        waitingForResult = false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    protected boolean finishAtMenuNavigation() {
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            getFetLifeApplication().getUserSessionManager().onUserLogOut();
            LoginActivity.startLogin(getFetLifeApplication());
        } else if (id == R.id.nav_conversations) {
            ConversationsActivity.startActivity(this);
        } else if (id == R.id.nav_friends) {
            FriendsActivity.startActivity(this);
        } else if (id == R.id.nav_friendrequests) {
            FriendRequestsActivity.startActivity(this, false);
        } else if (id == R.id.nav_introduce) {
            AddNfcFriendActivity.startActivity(this);
        } else if (id == R.id.nav_about) {
            AboutActivity.startActivity(this);
        } else if (id == R.id.nav_notifications) {
            NotificationHistoryActivity.startActivity(this, false);
        } else if (id == R.id.nav_upload_pic) {
            MediaUploadSelectionDialog.show(this);
        } else if (id == R.id.nav_settings) {
            SettingsActivity.startActivity(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (isNavigation(id) && finishAtMenuNavigation()) {
            finish();
        }

        return false;
    }

    private boolean isNavigation(int id) {
        return id != R.id.nav_upload_pic;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onKeyDown(keyCode, e);
    }

    protected void showProgress() {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    protected void dismissProgress() {
        progressIndicator.setVisibility(View.INVISIBLE);
    }

    protected void verifyUser() {
        if (getFetLifeApplication().getUserSessionManager().getCurrentUser() == null) {
            LoginActivity.startLogin(getFetLifeApplication());
            finish();
            return;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthenticationFailed(AuthenticationFailedEvent authenticationFailedEvent) {
        showToast(getString(R.string.authentication_failed));
        getFetLifeApplication().getUserSessionManager().onUserLogOut();
        LoginActivity.startLogin(getFetLifeApplication());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent.getServiceCallAction().equals(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE)) {
            showToast(getString(R.string.message_image_upload_failed));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction().equals(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE)) {
            showToast(getString(R.string.message_image_upload_finished));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent.getServiceCallAction().equals(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE)) {
            showToast(getString(R.string.message_image_upload_started));
        }
    }

    public void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ResourceListActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplication();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        waitingForResult = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        waitingForResult = false;
    }

    public boolean isWaitingForResult() {
        return waitingForResult;
    }

    public void onWaitingForResult() {
        this.waitingForResult = true;
    }
}
