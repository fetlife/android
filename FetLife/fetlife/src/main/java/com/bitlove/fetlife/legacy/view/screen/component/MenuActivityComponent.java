package com.bitlove.fetlife.legacy.view.screen.component;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.legacy.view.dialog.PictureUploadSelectionDialog;
import com.bitlove.fetlife.legacy.view.dialog.VideoUploadSelectionDialog;
import com.bitlove.fetlife.legacy.view.screen.BaseActivity;
import com.bitlove.fetlife.legacy.view.screen.resource.EventsActivity;
import com.bitlove.fetlife.legacy.view.screen.resource.ExploreActivity;
import com.bitlove.fetlife.legacy.view.screen.resource.NotificationHistoryActivity;
import com.bitlove.fetlife.legacy.view.screen.resource.groups.GroupsActivity;
import com.bitlove.fetlife.legacy.view.screen.resource.profile.ProfileActivity;
import com.bitlove.fetlife.legacy.view.screen.standalone.ReleaseNotesActivity;
import com.bitlove.fetlife.legacy.view.screen.standalone.SettingsActivity;
import com.bitlove.fetlife.webapp.navigation.WebAppNavigation;
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.navigation.NavigationView;

public class MenuActivityComponent extends ActivityComponent {

    private BaseActivity menuActivity;

    protected NavigationView navigationView;
    protected View navigationHeaderView;

    @Override
    public void onActivityCreated(BaseActivity baseActivity, Bundle savedInstanceState) {

        this.menuActivity = baseActivity;

        Toolbar toolbar = (Toolbar) menuActivity.findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) menuActivity.findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) menuActivity.findViewById(R.id.navigation_side_layout);

        if (drawer == null || navigationView == null || (navigationHeaderView = navigationView.getHeaderView(0)) == null) {
            return;
        }

        if (toolbar != null) {
            menuActivity.setSupportActionBar(toolbar);
        }

        navigationView.setNavigationItemSelectedListener(baseActivity);

        final Member currentUser = menuActivity.getFetLifeApplication().getUserSessionManager().getCurrentUser();
        if (currentUser != null) {
            TextView headerTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_text);
            headerTextView.setText(currentUser.getNickname());
            TextView headerSubTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_subtext);
            headerSubTextView.setText(currentUser.getMetaInfo());
            SimpleDraweeView headerAvatar = (SimpleDraweeView) navigationHeaderView.findViewById(R.id.nav_header_image);
            headerAvatar.setImageURI(currentUser.getAvatarLink());
            headerAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileActivity.startActivity(menuActivity,currentUser.getId());
                }
            });
        }
    }

    @Override
    public Boolean onActivityOptionsItemSelected(BaseActivity baseActivity, MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return false;
    }

    @Override
    public Boolean onActivityCreateOptionsMenu(BaseActivity baseActivity, Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuActivity.getMenuInflater().inflate(R.menu.activity_resource, menu);
        // Set an icon in the ActionBar
        return true;
    }

    @Override
    public Boolean onActivityBackPressed(BaseActivity baseActivity) {
        DrawerLayout drawer = (DrawerLayout) menuActivity.findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(Gravity.RIGHT);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean onActivityKeyDown(BaseActivity baseActivity, int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            DrawerLayout drawer = (DrawerLayout) menuActivity.findViewById(R.id.drawer_layout);
            if (!drawer.isDrawerOpen(Gravity.RIGHT)) {
                drawer.openDrawer(Gravity.RIGHT);
                return true;
            }
        }
        return null;
    }

    @Override
    public Boolean onActivityNavigationItemSelected(BaseActivity baseActivity, MenuItem item) {

        menuActivity.setFinishAfterNavigation(false);
        DrawerLayout drawer = (DrawerLayout) menuActivity.findViewById(R.id.drawer_layout);
        Intent pendingNavigationIntent = null;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            logEvent("nav_logout");
            menuActivity.getFetLifeApplication().getUserSessionManager().deleteCurrentUserDb();
            menuActivity.getFetLifeApplication().getUserSessionManager().onUserLogOut();
            menuActivity.finish();
            FetLifeWebViewActivity.Companion.startLogin(menuActivity.getFetLifeApplication(), null);
            return false;
        } else if (id == R.id.nav_places) {
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/places", true, true,null,false, null);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_search) {
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/search", true, true,null,false, null);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_about) {
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/android", true, true,null,false, null);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_relnotes) {
            pendingNavigationIntent = ReleaseNotesActivity.createIntent(menuActivity);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_app_notifications) {
            pendingNavigationIntent = NotificationHistoryActivity.createIntent(menuActivity,false);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_upload_pic) {
            logEvent("nav_upload_pic");
            if (isStoragePermissionGranted()) {
                PictureUploadSelectionDialog.show(menuActivity);
            } else {
                requestStoragePermission(BaseActivity.PERMISSION_REQUEST_PICTURE_UPLOAD);
            }
        } else if (id == R.id.nav_upload_video) {
            logEvent("nav_upload_video");
            if (isStoragePermissionGranted()) {
                VideoUploadSelectionDialog.show(menuActivity);
            } else {
                requestStoragePermission(BaseActivity.PERMISSION_REQUEST_VIDEO_UPLOAD);
            }
        } else if (id == R.id.nav_settings) {
            pendingNavigationIntent = SettingsActivity.createIntent(menuActivity);
        } else if (id == R.id.nav_websettings) {
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/settings/account", true, true,null,false, null);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_stuff_you_love) {
            pendingNavigationIntent = ExploreActivity.createIntent(menuActivity, ExploreActivity.Explore.STUFF_YOU_LOVE);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_fresh_and_pervy) {
            pendingNavigationIntent = ExploreActivity.createIntent(menuActivity, ExploreActivity.Explore.FRESH_AND_PERVY);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_kinky_and_popular) {
            pendingNavigationIntent = ExploreActivity.createIntent(menuActivity, ExploreActivity.Explore.KINKY_AND_POPULAR);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_help) {
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/help", true, true,null,false, null);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_guidelines) {
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/guidelines", true, true,null,false, null);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_contact) {
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/contact", true, true,null,false, null);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_support) {
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/support", true, true,null,false, null);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_ads) {
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/ads", true, true,null,false, null);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_glossary) {
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/glossary", true, true,null,false, null);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_team) {
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/team", true, true,null,false, null);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_events) {
            menuActivity.setFinishAfterNavigation(true);
            if (isLocationPermissionGranted()) {
                pendingNavigationIntent = EventsActivity.createIntent(menuActivity);
            } else {
                requestLocationPermission(BaseActivity.PERMISSION_REQUEST_LOCATION);
            }
        } else if (id == R.id.nav_questions) {
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/q", true, true,null,false, null);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_groups) {
           pendingNavigationIntent = GroupsActivity.createIntent(menuActivity,false);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_wallpaper) {
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/wallpapers", true, true,null,false, null);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_updates) {
            logEvent("nav_updates");
            menuActivity.showToast(menuActivity.getString(R.string.message_toast_checking_for_updates));
            FetLifeApiIntentService.startApiCall(menuActivity,FetLifeApiIntentService.ACTION_EXTERNAL_CALL_CHECK_4_UPDATES,Boolean.toString(true));
        }

        menuActivity.setPendingNavigationIntent(pendingNavigationIntent);
        drawer.closeDrawer(Gravity.RIGHT);

        return false;
    }

    private void logEvent(String item) {
        Answers.getInstance().logCustom(
                new CustomEvent("Menu Item: " + item + " selected"));
    }

    private void requestStoragePermission(int requestAction) {
        ActivityCompat.requestPermissions(menuActivity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                requestAction);
    }

    private boolean isStoragePermissionGranted() {
        return ContextCompat.checkSelfPermission(menuActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission(int requestAction) {
        ActivityCompat.requestPermissions(menuActivity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                requestAction);
    }

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(menuActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(BaseActivity baseActivity, int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(baseActivity, requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case BaseActivity.PERMISSION_REQUEST_PICTURE_UPLOAD:
                    PictureUploadSelectionDialog.show(menuActivity);
                    break;
                case BaseActivity.PERMISSION_REQUEST_VIDEO_UPLOAD:
                    VideoUploadSelectionDialog.show(menuActivity);
                    break;
                case BaseActivity.PERMISSION_REQUEST_LOCATION:
                    EventsActivity.startActivity(menuActivity);
                    break;
                default:
                    break;
            }
        } else {
            switch (requestCode) {
                case BaseActivity.PERMISSION_REQUEST_LOCATION:
                    EventsActivity.startActivity(menuActivity);
                    break;
                default:
                    return;
            }
        }
    }
}
