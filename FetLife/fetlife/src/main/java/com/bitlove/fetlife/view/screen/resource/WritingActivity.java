package com.bitlove.fetlife.view.screen.resource;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Writing;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.StringUtil;
import com.bitlove.fetlife.util.UrlUtil;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;
import com.bitlove.fetlife.view.widget.FlingBehavior;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WritingActivity extends ResourceActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final int PROFILE_MENU_HITREC_PADDING = 200;

    private static final String EXTRA_WRITINGID = "EXTRA_WRITINGID";
    private static final String EXTRA_MEMBERID = "EXTRA_MEMBERID";
    private String memberId;

    private Writing writing;
    private String writingId;

    public static void startActivity(BaseActivity baseActivity, String writingId, String memberId) {
        Intent intent = new Intent(baseActivity, WritingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(EXTRA_WRITINGID, writingId);
        intent.putExtra(EXTRA_MEMBERID, memberId);
        baseActivity.startActivity(intent);
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        memberId = getIntent().getStringExtra(EXTRA_MEMBERID);
        writingId = getIntent().getStringExtra(EXTRA_WRITINGID);
        writing = Writing.loadWriting(writingId);

        if (writing != null) {
            setWritingDetails(writing);
        }

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        params.setBehavior(new FlingBehavior());
        appBarLayout.addOnOffsetChangedListener(this);

        findViewById(R.id.writing_menu_icon_author_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewAuthor(v);
            }
        });

        findViewById(R.id.writing_menu_icon_view_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewWriting(v);
            }
        });
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("");
        getSupportActionBar().setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        TextView headerTitle = (TextView) findViewById(R.id.writing_title);
        toolbarTitle.setText(title);
        headerTitle.setText(title);
    }

    private void setWritingDetails(final Writing writing) {
        setTitle(writing.getTitle());
        TextView body = (TextView) findViewById(R.id.text_writing_body);
        body.setText(writing.getHtmlBody());

        findViewById(R.id.writing_menu_icon_love_container).setVisibility(writing.isDetailLoaded() ? View.VISIBLE : View.INVISIBLE);
        ImageView writingLove = (ImageView) findViewById(R.id.writing_menu_icon_love);
        writingLove.setImageResource(writing.isLovedByMe() ? R.drawable.ic_loved : R.drawable.ic_love);
        writingLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writing == null) {
                    return;
                }
                ImageView writingLove = (ImageView) v;
                boolean isLoved = writing.isLovedByMe();
                boolean newIsLoved = !isLoved;
                writingLove.setImageResource(newIsLoved ? R.drawable.ic_loved : R.drawable.ic_love);
                Writing.startLoveCallWithObserver(FetLifeApplication.getInstance(), writing, newIsLoved);
                writing.setLovedByMe(newIsLoved);
                writing.save();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (isRelatedCall(serviceCallStartedEvent.getServiceCallAction(), serviceCallStartedEvent.getParams())) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (isRelatedCall(serviceCallFinishedEvent.getServiceCallAction(), serviceCallFinishedEvent.getParams())) {
            final String writingId = getIntent().getStringExtra(EXTRA_WRITINGID);
            writing = Writing.loadWriting(writingId);
            setWritingDetails(writing);
            if (!isRelatedCall(FetLifeApiIntentService.getActionInProgress(), FetLifeApiIntentService.getInProgressActionParams())) {
                dismissProgress();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (isRelatedCall(serviceCallFailedEvent.getServiceCallAction(), serviceCallFailedEvent.getParams())) {
            dismissProgress();
        }
    }

    private boolean isRelatedCall(String serviceCallAction, String[] params) {
        if (params != null && params.length > 0 && writingId != null && !writingId.equals(params[0])) {
            return false;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_WRITING.equals(serviceCallAction)) {
            return true;
        }
        return false;
    }

    public void onViewAuthor(View v) {
        ProfileActivity.startActivity(this,memberId);
    }

    public void onViewWriting(View v) {
        if (writing != null) {
            UrlUtil.openUrl(this,writing.getUrl());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResourceStart() {
        FetLifeApiIntentService.startApiCall(this,FetLifeApiIntentService.ACTION_APICALL_WRITING,writingId,memberId);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreateActivityComponents() {
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_writing);
    }

    private static final float PERCENTAGE_TO_SHOW_TITLE_DETAILS = 0.7f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private static final long ALPHA_ANIMATIONS_DELAY = 200l;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        setToolbarVisibility(appBarLayout, findViewById(R.id.toolbar_title), percentage);
    }

    private boolean isTitleVisible = false;

    private void setToolbarVisibility(AppBarLayout appBarLayout, View title, float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_DETAILS) {
            if (!isTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.VISIBLE);
                isTitleVisible = true;
            }
        } else {
            if (isTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.INVISIBLE);
                isTitleVisible = false;
            }
        }
    }

    public static void startAlphaAnimation(final View v, long duration, long delay, final int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setStartOffset(delay);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

}
