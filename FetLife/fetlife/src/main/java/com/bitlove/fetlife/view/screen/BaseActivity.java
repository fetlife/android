package com.bitlove.fetlife.view.screen;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.view.screen.component.ActivityComponent;
import com.bitlove.fetlife.AnswersWrapper;
import com.bitlove.fetlife.CustomEventWrapper;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int PERMISSION_REQUEST_PICTURE_UPLOAD = 10000;
    public static final int PERMISSION_REQUEST_VIDEO_UPLOAD = 20000;
    public static final int PERMISSION_REQUEST_LOCATION = 30000;

    public static final String EXTRA_NOTIFICATION_SOURCE_TYPE = "EXTRA_NOTIFICATION_SOURCE_TYPE";

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

        logEvent();

        if (savedInstanceState == null) {
            String notificationSourceType = getIntent().getStringExtra(EXTRA_NOTIFICATION_SOURCE_TYPE);
            if (notificationSourceType != null) {
                getFetLifeApplication().getNotificationParser().clearNotification(notificationSourceType);
            }
        }

        onCreateActivityComponents();
        onSetContentView();

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
    }

    protected void logEvent() {
        AnswersWrapper.getInstance().logCustom(
                new CustomEventWrapper(BaseActivity.this.getClass().getSimpleName()));
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

}
