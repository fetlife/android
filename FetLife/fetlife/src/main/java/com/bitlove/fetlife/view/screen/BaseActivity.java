package com.bitlove.fetlife.view.screen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bitlove.fetlife.view.screen.resource.ConversationsActivity;
import com.bitlove.fetlife.view.screen.resource.FeedActivity;
import com.bitlove.fetlife.view.screen.resource.TurboLinksViewActivity;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.transition.Transition;

import android.transition.Fade;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.view.screen.component.ActivityComponent;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.resources.TextAppearance;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int PERMISSION_REQUEST_PICTURE_UPLOAD = 10000;
    public static final int PERMISSION_REQUEST_VIDEO_UPLOAD = 20000;
    public static final int PERMISSION_REQUEST_LOCATION = 30000;

    public static final String EXTRA_NOTIFICATION_SOURCE_TYPE = "EXTRA_NOTIFICATION_SOURCE_TYPE";
    public static final String EXTRA_SELECTED_BOTTOM_NAV_ITEM = "EXTRA_SELECTED_BOTTOM_NAV_ITEM";

    protected boolean waitingForResult;
    protected ProgressBar progressIndicator;
    protected SimpleDraweeView toolBarImage;
    protected TextView toolBarTitle;

    List<ActivityComponent> activityComponentList = new ArrayList<>();

    protected void addActivityComponent(ActivityComponent activityComponent) {
        activityComponentList.add(activityComponent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        getWindow().setSharedElementExitTransition(makeEnterTransition());
////        getWindow().setEnterTransition(null);
////        getWindow().setExitTransition(null);
//        getWindow().setAllowEnterTransitionOverlap(false);
//        getWindow().setAllowReturnTransitionOverlap(false);
        logEvent();

        if (savedInstanceState == null) {
            String notificationSourceType = getIntent().getStringExtra(EXTRA_NOTIFICATION_SOURCE_TYPE);
            if (notificationSourceType != null) {
                getFetLifeApplication().getNotificationParser().clearNotification(notificationSourceType);
            }
        }

        onCreateActivityComponents();
        onSetContentView();

//        getWindow().setEnterTransition(makeExcludeTransition());
//        getWindow().setExitTransition(makeExcludeTransition());

        TextView previewText = (TextView)findViewById(R.id.text_preview);
        if (previewText != null) {
            if (BuildConfig.PREVIEW) {
                RotateAnimation rotate= (RotateAnimation) AnimationUtils.loadAnimation(this,R.anim.preview_rotation);
                previewText.setAnimation(rotate);
                previewText.setVisibility(View.VISIBLE);
            } else {
                previewText.setVisibility(View.GONE);
            }
        }

        for (ActivityComponent activityComponent : activityComponentList) {
            activityComponent.onActivityCreated(this, savedInstanceState);
        }

        final BottomNavigationView bottomNavigation = findViewById(R.id.navigation_bottom);
        if (bottomNavigation != null) {
            IconicsMenuInflaterUtil.inflate(getMenuInflater(), this, R.menu.menu_navigation_bottom, bottomNavigation.getMenu());
            final int selectedMenuItem = getIntent().getIntExtra(EXTRA_SELECTED_BOTTOM_NAV_ITEM,-1);
            if (selectedMenuItem < 0) {
                bottomNavigation.setVisibility(View.GONE);
            } else {
                bottomNavigation.setVisibility(View.VISIBLE);
                bottomNavigation.setSelectedItemId(selectedMenuItem);

                final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

                final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Intent intent;
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(BaseActivity.this, bottomNavigation, "bottomNavBar");
                        switch (menuItem.getItemId()) {
                            case R.id.navigation_bottom_feed:
                                bottomNavigation.setOnNavigationItemSelectedListener(null);
                                intent = new Intent(BaseActivity.this, FeedActivity.class);
                                intent.putExtra(EXTRA_SELECTED_BOTTOM_NAV_ITEM,menuItem.getItemId());
                                BaseActivity.this.startActivity(intent,options.toBundle());
                                //FeedActivity.startActivity(BaseActivity.this, bottomNavigation, "bottomNavBar");
//                                finishAfterTransition();
                                break;
                            case R.id.navigation_bottom_inbox:
                                bottomNavigation.setOnNavigationItemSelectedListener(null);
                                intent = new Intent(BaseActivity.this, ConversationsActivity.class);
                                intent.putExtra(EXTRA_SELECTED_BOTTOM_NAV_ITEM,menuItem.getItemId());
                                BaseActivity.this.startActivity(intent,options.toBundle());
//                                finishAfterTransition();
//                            ConversationsActivity.startActivity(BaseActivity.this, null, false, bottomNavigation, "bottomNavBar");
                                break;
                            case R.id.navigation_bottom_requests:
                                bottomNavigation.setOnNavigationItemSelectedListener(null);
                                TurboLinksViewActivity.startActivity(BaseActivity.this,"requests",BaseActivity.this.getString(R.string.title_activity_friendrequests),R.id.navigation_bottom_requests,options.toBundle());
//                                finishAfterTransition();
                                break;
                            case R.id.navigation_bottom_notifications:
                                bottomNavigation.setOnNavigationItemSelectedListener(null);
                                TurboLinksViewActivity.startActivity(BaseActivity.this,"notifications",BaseActivity.this.getString(R.string.title_activity_notifications),R.id.navigation_bottom_notifications,options.toBundle());
  //                              finishAfterTransition();
                                break;
                            case R.id.navigation_bottom_menu:
                                if (drawerLayout != null) {
                                    if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                                        if (selectedMenuItem > 0) {
                                            bottomNavigation.setOnNavigationItemSelectedListener(null);
                                            bottomNavigation.setSelectedItemId(selectedMenuItem);
                                            bottomNavigation.setOnNavigationItemSelectedListener(this);
                                        }
                                        drawerLayout.closeDrawer(Gravity.RIGHT);
                                    } else {
                                        bottomNavigation.setOnNavigationItemSelectedListener(null);
                                        bottomNavigation.setSelectedItemId(R.id.navigation_bottom_menu);
                                        bottomNavigation.setOnNavigationItemSelectedListener(this);
                                        drawerLayout.openDrawer(Gravity.RIGHT);
                                    }
                                }

                        }
                        return false;
                    }
                };
                bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
                drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {
                        bottomNavigation.setOnNavigationItemSelectedListener(null);
                        bottomNavigation.setSelectedItemId(R.id.navigation_bottom_menu);
                        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
                    }

                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        if (selectedMenuItem > 0) {
                            bottomNavigation.setOnNavigationItemSelectedListener(null);
                            bottomNavigation.setSelectedItemId(selectedMenuItem);
                            bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
                        }
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });

            }
        }
    }

    protected void logEvent() {
        Answers.getInstance().logCustom(
                new CustomEvent(BaseActivity.this.getClass().getSimpleName()));
    }

    protected abstract void onCreateActivityComponents();

    protected abstract void onSetContentView();

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initProgressIndicator();
        toolBarImage = (SimpleDraweeView) findViewById(R.id.toolbar_image);
        toolBarTitle = (TextView) findViewById(R.id.toolbar_title);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initProgressIndicator();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("");
        if (toolBarTitle != null) {
            toolBarTitle.setText(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle("");
        if (toolBarTitle != null) {
            toolBarTitle.setText(titleId);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initProgressIndicator();
    }

    protected void initProgressIndicator() {
        progressIndicator = (ProgressBar) findViewById(R.id.toolbar_progress_indicator);
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (ActivityComponent activityComponent : activityComponentList) {
            activityComponent.onActivityPaused(this);
        }
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (ActivityComponent activityComponent : activityComponentList) {
            activityComponent.onActivityResumed(this);
        }
        waitingForResult = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getFetLifeApplication().getEventBus().register(this);
        for (ActivityComponent activityComponent : activityComponentList) {
            activityComponent.onActivityStarted(this);
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        getFetLifeApplication().getEventBus().unregister(this);
        for (ActivityComponent activityComponent : activityComponentList) {
            activityComponent.onActivityStopped(this);
        }
        BaseActivity baseActivity = (BaseActivity) getFetLifeApplication().getForegroundActivity();
        final int selectedMenuItem = getIntent().getIntExtra(EXTRA_SELECTED_BOTTOM_NAV_ITEM,-1);
        if (selectedMenuItem >= 0 && baseActivity != null && baseActivity.getIntent().getIntExtra(EXTRA_SELECTED_BOTTOM_NAV_ITEM,-1) >= 0) {
//            finishAfterTransition();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (ActivityComponent activityComponent : activityComponentList) {
            activityComponent.onActivityDestroyed(this);
        }
        waitingForResult = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        for (ActivityComponent activityComponent : activityComponentList) {
            activityComponent.onActivitySaveInstanceState(this, outState);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Boolean result = null;
        for (ActivityComponent activityComponent : activityComponentList) {
            Boolean componentResult = activityComponent.onActivityOptionsItemSelected(this, item);
            if (componentResult == null) {
                continue;
            }
            if (result == null) {
                result = componentResult;
                continue;
            }
            result |= componentResult;
        }
        if (result == null) {
            return super.onOptionsItemSelected(item);
        }
        return result;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Boolean result = false;
        for (ActivityComponent activityComponent : activityComponentList) {
            Boolean componentResult = activityComponent.onActivityNavigationItemSelected(this, item);
            if (componentResult == null) {
                continue;
            }
            if (result == null) {
                result = componentResult;
                continue;
            }
            result |= componentResult;
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        Boolean result = null;
        for (ActivityComponent activityComponent : activityComponentList) {
            Boolean componentResult = activityComponent.onActivityBackPressed(this);
            if (componentResult == null) {
                continue;
            }
            if (result == null) {
                result = componentResult;
                continue;
            }
            result |= componentResult;
        }
        if (result == null || !result) {
            final int selectedMenuItem = getIntent().getIntExtra(EXTRA_SELECTED_BOTTOM_NAV_ITEM,-1);
            if (selectedMenuItem >= 0) {
                finish();
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Boolean result = null;
        for (ActivityComponent activityComponent : activityComponentList) {
            Boolean componentResult = activityComponent.onActivityCreateOptionsMenu(this, menu);
            if (componentResult == null) {
                continue;
            }
            if (result == null) {
                result = componentResult;
                continue;
            }
            result |= componentResult;
        }
        if (result == null) {
            return super.onCreateOptionsMenu(menu);
        }
        return result;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        Boolean result = null;
        for (ActivityComponent activityComponent : activityComponentList) {
            Boolean componentResult = activityComponent.onActivityKeyDown(this, keyCode, e);
            if (componentResult == null) {
                continue;
            }
            if (result == null) {
                result = componentResult;
                continue;
            }
            result |= componentResult;
        }
        if (result == null  || !result) {
            return super.onKeyDown(keyCode, e);
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (ActivityComponent activityComponent : activityComponentList) {
            activityComponent.onActivityResult(this, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (ActivityComponent activityComponent : activityComponentList) {
            activityComponent.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        waitingForResult = true;
    }

    public boolean isWaitingForResult() {
        return waitingForResult;
    }

    public void onWaitingForResult() {
        this.waitingForResult = true;
    }

    public void showProgress() {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    public void dismissProgress() {
        progressIndicator.setVisibility(View.INVISIBLE);
    }

    public void showToast(final String text) {
        getFetLifeApplication().showToast(text);
    }

    public FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplication();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    public static Transition makeExcludeTransition() {
        Transition fade = new Fade();
        fade.excludeTarget(R.id.app_bar, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        return fade;
    }
}
