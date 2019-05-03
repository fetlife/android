package com.bitlove.fetlife.view.screen.component;

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

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.LogUtil;
import com.bitlove.fetlife.view.dialog.PictureUploadSelectionDialog;
import com.bitlove.fetlife.view.dialog.VideoUploadSelectionDialog;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.EventsActivity;
import com.bitlove.fetlife.view.screen.resource.ExploreActivity;
import com.bitlove.fetlife.view.screen.resource.NotificationHistoryActivity;
import com.bitlove.fetlife.view.screen.resource.groups.GroupsActivity;
import com.bitlove.fetlife.view.screen.resource.members.MembersActivity;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;
import com.bitlove.fetlife.view.screen.standalone.LoginActivity;
import com.bitlove.fetlife.view.screen.standalone.ReleaseNotesActivity;
import com.bitlove.fetlife.view.screen.standalone.SettingsActivity;
import com.bitlove.fetlife.webapp.navigation.WebAppNavigation;
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
//        navigationView.getMenu().findItem(R.id.nav_feed).setIcon(
//                MaterialDrawableBuilder.with(menuActivity)
//                        .setIcon(MaterialDrawableBuilder.IconValue.VIEW_LIST)
//                        .setColor(R.color.text_color_secondary)
//                        .setToActionbarSize().build());
//        navigationView.getMenu().findItem(R.id.nav_upload_video).setIcon(
//                MaterialDrawableBuilder.with(menuActivity)
//                        .setIcon(MaterialDrawableBuilder.IconValue.VIDEO)
//                        .setColor(R.color.text_color_secondary)
//                        .setToActionbarSize().build());

        if (drawer == null || navigationView == null || (navigationHeaderView = navigationView.getHeaderView(0)) == null) {
            return;
        }

        if (toolbar != null) {
            menuActivity.setSupportActionBar(toolbar);
        }

//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                menuActivity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                InputMethodManager inputMethodManager = (InputMethodManager)
//                        menuActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(menuActivity.getCurrentFocus().getWindowToken(), 0);
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//                InputMethodManager inputMethodManager = (InputMethodManager)
//                        menuActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(menuActivity.getCurrentFocus().getWindowToken(), 0);
//            }
//        };
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

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
            LoginActivity.startLogin(menuActivity.getFetLifeApplication());
            return false;
//        } else if (id == R.id.nav_conversations) {
//            ConversationsActivity.startActivity(menuActivity, false);
        } else if (id == R.id.nav_show_logs) {
            LogUtil.shareLogs(menuActivity);
            return false;
        } else if (id == R.id.nav_places) {
//            pendingNavigationIntent = TurboLinksViewActivity.createIntent(menuActivity,"places",menuActivity.getString(R.string.title_activity_places), true, null, false);
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/places", true,null,false);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_search) {
//            pendingNavigationIntent = TurboLinksViewActivity.createIntent(menuActivity,"search",menuActivity.getString(R.string.title_activity_search), true, null, false);
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/search", true,null,false);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_members) {
            pendingNavigationIntent = MembersActivity.createIntent(menuActivity,false);
            menuActivity.setFinishAfterNavigation(true);
//        } else if (id == R.id.nav_friendrequests) {
//            TurboLinksViewActivity.startActivity(menuActivity,"requests",menuActivity.getString(R.string.title_activity_friendrequests));
//        } else if (id == R.id.nav_introduce) {
//            logEvent("nav_introduce");
//            AddNfcFriendActivity.startActivity(menuActivity);
        } else if (id == R.id.nav_about) {
//            pendingNavigationIntent = TurboLinksViewActivity.createIntent(menuActivity,"android",menuActivity.getString(R.string.title_activity_about), true, null, false);
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/android", true,null,false);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_relnotes) {
            pendingNavigationIntent = ReleaseNotesActivity.createIntent(menuActivity);
            menuActivity.setFinishAfterNavigation(true);
//        } else if (id == R.id.nav_notifications) {
//            TurboLinksViewActivity.startActivity(menuActivity,"notifications",menuActivity.getString(R.string.title_activity_notifications));
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
            //menuActivity.setFinishAfterNavigation(true);
//        } else if (id == R.id.nav_feed) {
//            FeedActivity.startActivity(menuActivity);
        } else if (id == R.id.nav_websettings) {
//            pendingNavigationIntent = TurboLinksViewActivity.createIntent(menuActivity,"settings/account",menuActivity.getString(R.string.title_activity_websettings), true, null, false);
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/settings/account", true,null,false);
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
//            UrlUtil.openUrl(menuActivity,"https://app.fetlife.com/help",true,true);
//            pendingNavigationIntent = TurboLinksViewActivity.createIntent(menuActivity,"help",menuActivity.getString(R.string.title_activity_help), true, null, false);
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/help", true,null,false);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_guidelines) {
//            pendingNavigationIntent = TurboLinksViewActivity.createIntent(menuActivity,"guidelines",menuActivity.getString(R.string.title_activity_guidelines), true, null, false);
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/guidelines", true,null,false);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_contact) {
//            pendingNavigationIntent = TurboLinksViewActivity.createIntent(menuActivity,"contact",menuActivity.getString(R.string.title_activity_contact), true, null, false);
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/contact", true,null,false);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_support) {
//            pendingNavigationIntent = TurboLinksViewActivity.createIntent(menuActivity,"support",menuActivity.getString(R.string.title_activity_support), true, null, false);
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/support", true,null,false);
            menuActivity.setFinishAfterNavigation(true);
//        } else if (id == R.id.nav_search) {
//            TurboLinksViewActivity.startActivity(menuActivity,"search",menuActivity.getString(R.string.title_activity_search));
        } else if (id == R.id.nav_ads) {
//            pendingNavigationIntent = TurboLinksViewActivity.createIntent(menuActivity,"ads",menuActivity.getString(R.string.title_activity_ads), true, null, false);
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/ads", true,null,false);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_glossary) {
//            pendingNavigationIntent = TurboLinksViewActivity.createIntent(menuActivity,"glossary",menuActivity.getString(R.string.title_activity_glossary), true, null, false);
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/glossary", true,null,false);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_team) {
//            pendingNavigationIntent = TurboLinksViewActivity.createIntent(menuActivity,"team",menuActivity.getString(R.string.title_activity_team), true, null, false);
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/team", true,null,false);
            menuActivity.setFinishAfterNavigation(true);
//        } else if (id == R.id.nav_wallpapers) {
//            TurboLinksViewActivity.startActivity(menuActivity,"wallpapers",menuActivity.getString(R.string.title_activity_wallpapers));
        } else if (id == R.id.nav_events) {
            menuActivity.setFinishAfterNavigation(true);
            if (isLocationPermissionGranted()) {
                pendingNavigationIntent = EventsActivity.createIntent(menuActivity);
            } else {
                requestLocationPermission(BaseActivity.PERMISSION_REQUEST_LOCATION);
            }
        } else if (id == R.id.nav_questions) {
//            pendingNavigationIntent = TurboLinksViewActivity.createIntent(menuActivity,"q",menuActivity.getString(R.string.title_activity_questions), true, null,false);
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/q", true,null,false);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_groups) {
           pendingNavigationIntent = GroupsActivity.createIntent(menuActivity,false);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_wallpaper) {
//            UrlUtil.openUrl(menuActivity,"https://app.fetlife.com/wallpapers", true);
//            pendingNavigationIntent = TurboLinksViewActivity.createIntent(menuActivity,"wallpapers",menuActivity.getString(R.string.title_activity_wallpapers), true, null,false);
            pendingNavigationIntent = FetLifeWebViewActivity.Companion.createIntent(menuActivity, WebAppNavigation.WEBAPP_BASE_URL + "/wallpapers", true,null,false);
            menuActivity.setFinishAfterNavigation(true);
        } else if (id == R.id.nav_updates) {
            logEvent("nav_updates");
            menuActivity.showToast(menuActivity.getString(R.string.message_toast_checking_for_updates));
            FetLifeApiIntentService.startApiCall(menuActivity,FetLifeApiIntentService.ACTION_EXTERNAL_CALL_CHECK_4_UPDATES,Boolean.toString(true));
        }

        menuActivity.setPendingNavigationIntent(pendingNavigationIntent);
        drawer.closeDrawer(Gravity.RIGHT);

//        if (isNavigation(id) && ((MenuActivityCallBack)menuActivity).finishAtMenuNavigation()) {
//            menuActivity.finish();
//        }

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

    private boolean isNavigation(int id) {
        return id != R.id.nav_upload_pic;
    }
}
