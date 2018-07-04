package com.bitlove.fetlife.view.screen.component;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.dialog.PictureUploadSelectionDialog;
import com.bitlove.fetlife.view.dialog.VideoUploadSelectionDialog;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.ConversationsActivity;
import com.bitlove.fetlife.view.screen.resource.EventsActivity;
import com.bitlove.fetlife.view.screen.resource.FeedActivity;
import com.bitlove.fetlife.view.screen.resource.ExploreActivity;
import com.bitlove.fetlife.view.screen.resource.groups.GroupsActivity;
import com.bitlove.fetlife.view.screen.resource.TurboLinksViewActivity;
import com.bitlove.fetlife.view.screen.resource.members.MembersActivity;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;
import com.bitlove.fetlife.view.screen.standalone.ReleaseNotesActivity;
import com.bitlove.fetlife.view.screen.standalone.AddNfcFriendActivity;
import com.bitlove.fetlife.view.screen.standalone.LoginActivity;
import com.bitlove.fetlife.view.screen.standalone.SettingsActivity;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.drawee.view.SimpleDraweeView;

public class MenuActivityComponent extends ActivityComponent {

    public interface MenuActivityCallBack {
        boolean finishAtMenuNavigation();
    }

    private BaseActivity menuActivity;

    protected NavigationView navigationView;
    protected View navigationHeaderView;

    @Override
    public void onActivityCreated(BaseActivity baseActivity, Bundle savedInstanceState) {

        this.menuActivity = baseActivity;

        if (!(menuActivity instanceof MenuActivityCallBack)) {
            throw new IllegalArgumentException();
        }

        Toolbar toolbar = (Toolbar) menuActivity.findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) menuActivity.findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) menuActivity.findViewById(R.id.nav_view);
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

        if (toolbar == null || drawer == null || navigationView == null || (navigationHeaderView = navigationView.getHeaderView(0)) == null) {
            return;
        }

        menuActivity.setSupportActionBar(toolbar);

        if (toolbar == null) {
            return;
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                menuActivity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        menuActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(menuActivity.getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        menuActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(menuActivity.getCurrentFocus().getWindowToken(), 0);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean onActivityKeyDown(BaseActivity baseActivity, int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            DrawerLayout drawer = (DrawerLayout) menuActivity.findViewById(R.id.drawer_layout);
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return null;
    }

    @Override
    public Boolean onActivityNavigationItemSelected(BaseActivity baseActivity, MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_logout) {
            logEvent("nav_logout");
            menuActivity.getFetLifeApplication().getUserSessionManager().deleteCurrentUserDb();
            menuActivity.getFetLifeApplication().getUserSessionManager().onUserLogOut();
            menuActivity.finish();
            LoginActivity.startLogin(menuActivity.getFetLifeApplication());
            return false;
        } else if (id == R.id.nav_conversations) {
            ConversationsActivity.startActivity(menuActivity, false);
        } else if (id == R.id.nav_members) {
            MembersActivity.startActivity(menuActivity, false);
        } else if (id == R.id.nav_friendrequests) {
            TurboLinksViewActivity.startActivity(menuActivity,"requests",menuActivity.getString(R.string.title_activity_friendrequests));
//        } else if (id == R.id.nav_introduce) {
//            logEvent("nav_introduce");
//            AddNfcFriendActivity.startActivity(menuActivity);
        } else if (id == R.id.nav_about) {
            TurboLinksViewActivity.startActivity(menuActivity,"android",menuActivity.getString(R.string.title_activity_about));
        } else if (id == R.id.nav_relnotes) {
            ReleaseNotesActivity.startActivity(menuActivity);
        } else if (id == R.id.nav_notifications) {
            TurboLinksViewActivity.startActivity(menuActivity,"notifications",menuActivity.getString(R.string.title_activity_notifications));
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
            SettingsActivity.startActivity(menuActivity);
        } else if (id == R.id.nav_feed) {
            FeedActivity.startActivity(menuActivity);
        } else if (id == R.id.nav_stuff_you_love) {
            ExploreActivity.startActivity(menuActivity, ExploreActivity.Explore.STUFF_YOU_LOVE);
        } else if (id == R.id.nav_fresh_and_pervy) {
            ExploreActivity.startActivity(menuActivity, ExploreActivity.Explore.FRESH_AND_PERVY);
        } else if (id == R.id.nav_kinky_and_popular) {
            ExploreActivity.startActivity(menuActivity, ExploreActivity.Explore.KINKY_AND_POPULAR);
        } else if (id == R.id.nav_support) {
            TurboLinksViewActivity.startActivity(menuActivity,"support",menuActivity.getString(R.string.title_activity_support));
//        } else if (id == R.id.nav_search) {
//            TurboLinksViewActivity.startActivity(menuActivity,"search",menuActivity.getString(R.string.title_activity_search));
        } else if (id == R.id.nav_ads) {
            TurboLinksViewActivity.startActivity(menuActivity,"ads",menuActivity.getString(R.string.title_activity_ads));
        } else if (id == R.id.nav_glossary) {
            TurboLinksViewActivity.startActivity(menuActivity,"glossary",menuActivity.getString(R.string.title_activity_glossary));
        } else if (id == R.id.nav_events) {
            if (isLocationPermissionGranted()) {
                EventsActivity.startActivity(menuActivity);
            } else {
                requestLocationPermission(BaseActivity.PERMISSION_REQUEST_LOCATION);
            }
        } else if (id == R.id.nav_groups) {
            GroupsActivity.startActivity(menuActivity,false);
        } else if (id == R.id.nav_updates) {
            logEvent("nav_updates");
            menuActivity.showToast(menuActivity.getString(R.string.message_toast_checking_for_updates));
            FetLifeApiIntentService.startApiCall(menuActivity,FetLifeApiIntentService.ACTION_EXTERNAL_CALL_CHECK_4_UPDATES,Boolean.toString(true));
        }

        DrawerLayout drawer = (DrawerLayout) menuActivity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (isNavigation(id) && ((MenuActivityCallBack)menuActivity).finishAtMenuNavigation()) {
            menuActivity.finish();
        }

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
