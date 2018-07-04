package com.bitlove.fetlife.view.screen.resource;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;
import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.api.FetLifeService;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Video;
import com.bitlove.fetlife.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.PictureUtil;
import com.bitlove.fetlife.util.ServerIdUtil;
import com.bitlove.fetlife.util.UrlUtil;
import com.bitlove.fetlife.view.adapter.feed.FeedItemResourceHelper;
import com.bitlove.fetlife.view.adapter.feed.FeedRecyclerAdapter;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.component.ActivityComponent;
import com.bitlove.fetlife.view.screen.component.MenuActivityComponent;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;
import com.bitlove.fetlife.view.screen.standalone.LoginActivity;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.common.util.UriUtil;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TurboLinksViewActivity extends ResourceActivity implements TurbolinksAdapter, MenuActivityComponent.MenuActivityCallBack, TurbolinksSession.ProgressObserver, TurbolinksSession.PageObserver {

    private static final Map<String,Integer> supportedBaseUrls = new HashMap<>();
    static {
        supportedBaseUrls.put("https://app.fetlife.com/ads",R.string.title_activity_ads);
        supportedBaseUrls.put("https://app.fetlife.com/support",R.string.title_activity_support);
        supportedBaseUrls.put("https://app.fetlife.com/glossary",R.string.title_activity_glossary);
        supportedBaseUrls.put("https://app.fetlife.com/notifications",R.string.title_activity_notifications);
        supportedBaseUrls.put("https://app.fetlife.com/requests",R.string.title_activity_friendrequests);
    }

    private static final String EXTRA_PAGE_URL = "EXTRA_PAGE_URL";
    private static final String EXTRA_PAGE_TITLE = "EXTRA_PAGE_TITLE";

    private TurbolinksView turbolinksView;
    private Set<String> requestedMediaIds = new HashSet<>();

    public static void startActivity(BaseActivity menuActivity, String pageUrl, String title) {
        Intent intent = new Intent(menuActivity,TurboLinksViewActivity.class);
        intent.putExtra(EXTRA_PAGE_URL, pageUrl);
        intent.putExtra(EXTRA_PAGE_TITLE, title);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        menuActivity.startActivity(intent);
    }

    @Override
    public boolean finishAtMenuNavigation() {
        return true;
    }

    @Override
    public void showProgress() {
        super.showProgress();
        turbolinksView.setVisibility(View.INVISIBLE);
    }

    protected void logEvent() {
        String pageUrl = getIntent().getStringExtra(EXTRA_PAGE_URL);
        Answers.getInstance().logCustom(
                new CustomEvent(getClass().getSimpleName() + ":" + pageUrl));
    }

    @Override
    public void hideProgress() {
        hideProgress(true);
    }

    public void hideProgress(final boolean showContent) {
        turbolinksView.setVisibility(showContent ? View.VISIBLE : View.INVISIBLE);
        dismissProgress();
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_turbolinksview);
    }

    @Override
    protected void onCreateActivityComponents() {
        addActivityComponent(new MenuActivityComponent());
    }

    @Override
    protected void onResourceStart() {
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        //        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle(getIntent().getStringExtra(EXTRA_PAGE_TITLE));

        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        String pageUrl = getIntent().getStringExtra(EXTRA_PAGE_URL);
        final String location = pageUrl.startsWith("https://") ? pageUrl : FetLifeService.BASE_URL + "/" + pageUrl;

        if (BuildConfig.DEBUG) {
            Log.d("TBLocation",location);
        }

        showProgress();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                TurbolinksSession.resetDefault();
                TurbolinksSession turbolinksSession = TurbolinksSession.getDefault(TurboLinksViewActivity.this);
                turbolinksSession.setDebugLoggingEnabled(BuildConfig.DEBUG);

                String accessToken = getFetLifeApplication().getUserSessionManager().getCurrentUser().getAccessToken();
                turbolinksSession.activity(TurboLinksViewActivity.this)
                        .adapter(TurboLinksViewActivity.this)
                        .view(turbolinksView)
                        .addProgressObserver(TurboLinksViewActivity.this)
                        .addPageObserver(TurboLinksViewActivity.this)
                        .restoreWithCachedSnapshot(false)
                        .setPullToRefreshEnabled(true)
                        .visitWithAuthHeader(location, FetLifeService.AUTH_HEADER_PREFIX + accessToken);

            }
        },33);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String location) {
        String mediaId = null;
        Uri uri = Uri.parse(location);
        if (!uri.getHost().equals(FetLifeService.HOST_NAME)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        } else if ((mediaId = UrlUtil.isMediaRequested(this,Uri.parse(location))) != null){
            requestedMediaIds.add(mediaId);
            return true;
        } else if (UrlUtil.handleInternal(this,uri)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onPageFinished() {
        hideProgress(true);
    }

    @Override
    public void onReceivedError(int errorCode) {
        hideProgress(false);
        TurbolinksSession.getDefault(this).visit("about:blank");
        TurbolinksSession.resetDefault();
        showToast(getString(R.string.error_apicall_failed));
    }

    @Override
    public void pageInvalidated() {

    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {
        hideProgress(false);
        TurbolinksSession.getDefault(this).visit("about:blank");
        TurbolinksSession.resetDefault();
        if (statusCode == 401) {
            LoginActivity.startLogin(getFetLifeApplication());
            showToast(getString(R.string.error_authentication_failed));
        } else {
            showToast(getString(R.string.error_apicall_failed));
        }
    }

    @Override
    public void visitCompleted() {
        hideProgress(true);
    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        if (BuildConfig.DEBUG) {
            Log.d("TBLocation",location);
        }

        String pageUrl = getIntent().getStringExtra(EXTRA_PAGE_URL);
        String baseLocation = FetLifeService.BASE_URL + "/" + pageUrl;

        String mediaId = null;
        if (!location.startsWith(baseLocation)) {
            Integer expectedTitleResourceId = supportedBaseUrls.get(location);
            if (expectedTitleResourceId != null) {
                TurboLinksViewActivity.startActivity(this,location,getString(expectedTitleResourceId));
                return;
            } else if ((mediaId = UrlUtil.isMediaRequested(this,Uri.parse(location))) != null){
                requestedMediaIds.add(mediaId);
                return;
            } else if (UrlUtil.handleInternal(this,Uri.parse(location))){
                return;
            } else {
                UrlUtil.openUrl(this,UrlUtil.removeAppIds(location));
                return;
            }
        } else {
            location = UrlUtil.removeAppIds(location);
        }

        TurbolinksView turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visitLocationWithAction(location,action);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView webView = TurbolinksSession.getDefault(this).getWebView();
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            TurbolinksSession.getDefault(this).getWebView().goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
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
            String mediaId = serviceCallFinishedEvent.getParams()[1];
            requestedMediaIds.remove(mediaId);

            if (ServerIdUtil.isServerId(mediaId)) {
                mediaId = ServerIdUtil.getLocalId(mediaId);
            }

            if (serviceCallFinishedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_MEMBER_PICTURE) {
                showPicture(mediaId);
            } else if (serviceCallFinishedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_MEMBER_VIDEO) {
                showVideo(mediaId);
            }

            if (!isRelatedCall(FetLifeApiIntentService.getActionInProgress(), FetLifeApiIntentService.getInProgressActionParams())) {
                dismissProgress();
            }
        }
    }

    private void showVideo(String mediaId) {
        Video video = Video.loadVideo(mediaId);
        Uri uri = Uri.parse(video.getVideoUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setDataAndType(uri, "video/*");
        startActivity(intent);
        init();
    }

    private void showPicture(String mediaId) {
        Picture picture = Picture.loadPicture(mediaId);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View overlay = inflater.inflate(R.layout.overlay_feed_imageswipe, null);
        final PictureUtil.OnPictureOverlayClickListener onPictureOverlayClickListener = new PictureUtil.OnPictureOverlayClickListener() {
            @Override
            public void onMemberClick(Member member) {
                member.mergeSave();
                ProfileActivity.startActivity(TurboLinksViewActivity.this,member.getId());
            }
            @Override
            public void onVisitPicture(Picture picture, String url) {
                UrlUtil.openUrl(TurboLinksViewActivity.this,url);
            }
            @Override
            public void onSharePicture(Picture picture, String url) {
                if (picture.isOnShareList()) {
                    Picture.unsharePicture(picture);
                } else {
                    Picture.sharePicture(picture);
                }
            }
        };
        PictureUtil.setOverlayContent(overlay, picture, onPictureOverlayClickListener);
        new ImageViewer.Builder(this, new String[]{picture.getDisplayUrl()}).setStartPosition(0).setOverlayView(overlay).show();
        init();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (isRelatedCall(serviceCallFailedEvent.getServiceCallAction(), serviceCallFailedEvent.getParams())) {
            requestedMediaIds.remove(serviceCallFailedEvent.getParams()[1]);
            dismissProgress();
        }
    }

    private boolean isRelatedCall(String serviceCallAction, String[] params) {
        if (params != null && params.length > 1 && !requestedMediaIds.contains(params[1])) {
            return false;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_PICTURE.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_VIDEO.equals(serviceCallAction)) {
            return true;
        }
        return false;
    }

}