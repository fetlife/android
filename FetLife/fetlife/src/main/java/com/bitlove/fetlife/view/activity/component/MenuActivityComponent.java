package com.bitlove.fetlife.view.activity.component;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.User;
import com.bitlove.fetlife.view.activity.standalone.AboutActivity;
import com.bitlove.fetlife.view.activity.standalone.AddNfcFriendActivity;
import com.bitlove.fetlife.view.activity.BaseActivity;
import com.bitlove.fetlife.view.activity.resource.ConversationsActivity;
import com.bitlove.fetlife.view.activity.resource.FriendRequestsActivity;
import com.bitlove.fetlife.view.activity.resource.FriendsActivity;
import com.bitlove.fetlife.view.activity.standalone.LoginActivity;
import com.bitlove.fetlife.view.activity.resource.NotificationHistoryActivity;
import com.bitlove.fetlife.view.activity.standalone.SettingsActivity;
import com.bitlove.fetlife.view.dialog.MediaUploadSelectionDialog;

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

        User currentUser = menuActivity.getFetLifeApplication().getUserSessionManager().getCurrentUser();
        if (currentUser != null) {
            TextView headerTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_text);
            headerTextView.setText(currentUser.getNickname());
            TextView headerSubTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_subtext);
            headerSubTextView.setText(currentUser.getMetaInfo());
            ImageView headerAvatar = (ImageView) navigationHeaderView.findViewById(R.id.nav_header_image);
            menuActivity.getFetLifeApplication().getImageLoader().loadImage(menuActivity, currentUser.getAvatarLink(), headerAvatar, R.drawable.dummy_avatar);
            final String selfLink = currentUser.getLink();
            if (selfLink != null) {
                headerAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(selfLink));
                        menuActivity.startActivity(intent);
                    }
                });
            }
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
            menuActivity.getFetLifeApplication().getUserSessionManager().onUserLogOut();
            LoginActivity.startLogin(menuActivity.getFetLifeApplication());
        } else if (id == R.id.nav_conversations) {
            ConversationsActivity.startActivity(menuActivity);
        } else if (id == R.id.nav_friends) {
            FriendsActivity.startActivity(menuActivity);
        } else if (id == R.id.nav_friendrequests) {
            FriendRequestsActivity.startActivity(menuActivity, false);
        } else if (id == R.id.nav_introduce) {
            AddNfcFriendActivity.startActivity(menuActivity);
        } else if (id == R.id.nav_about) {
            AboutActivity.startActivity(menuActivity);
        } else if (id == R.id.nav_notifications) {
            NotificationHistoryActivity.startActivity(menuActivity, false);
        } else if (id == R.id.nav_upload_pic) {
            MediaUploadSelectionDialog.show(menuActivity);
        } else if (id == R.id.nav_settings) {
            SettingsActivity.startActivity(menuActivity);
        }

        DrawerLayout drawer = (DrawerLayout) menuActivity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (isNavigation(id) && ((MenuActivityCallBack)menuActivity).finishAtMenuNavigation()) {
            menuActivity.finish();
        }

        return false;
    }

    private boolean isNavigation(int id) {
        return id != R.id.nav_upload_pic;
    }
}
