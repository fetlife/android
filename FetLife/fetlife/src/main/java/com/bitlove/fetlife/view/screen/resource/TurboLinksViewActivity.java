package com.bitlove.fetlife.view.screen.resource;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;
import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.inbound.onesignal.NotificationParser;
import com.bitlove.fetlife.model.api.FetLifeService;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Video;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.PictureUtil;
import com.bitlove.fetlife.util.ServerIdUtil;
import com.bitlove.fetlife.util.UrlUtil;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.component.MenuActivityComponent;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;
import com.bitlove.fetlife.view.screen.standalone.LoginActivity;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.hosopy.actioncable.Consumer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.appcompat.widget.Toolbar;

public class TurboLinksViewActivity extends ResourceActivity implements TurbolinksAdapter, TurbolinksSession.ProgressObserver, TurbolinksSession.PageObserver {

    private static final Map<String,Integer> supportedBaseUrls = new HashMap<>();

    public static final String FAB_LINK_NEW_QUESTION = "https://app.fetlife.com/q/new";

    public static final String URL_QUESTIONS_POPULAR = "https://fetlife.com/q?filter=popular";
    public static final String URL_QUESTIONS_TAGS = "https://fetlife.com/q/tags";
    public static final String URL_QUESTIONS = "https://fetlife.com/q";

    private static final String WEB_TITLE_SEPARATOR = " |";

    static {
//        supportedBaseUrls.put("https://app.fetlife.com/q",R.string.title_activity_questions);
//        supportedBaseUrls.put("https://fetlife.com/q",R.string.title_activity_questions);
        supportedBaseUrls.put("https://fetlife.com/places",R.string.title_activity_places);
        supportedBaseUrls.put("https://fetlife.com/settings",R.string.title_activity_websettings);
        supportedBaseUrls.put("https://fetlife.com/ads",R.string.title_activity_ads);
        supportedBaseUrls.put("https://fetlife.com/support",R.string.title_activity_support);
        supportedBaseUrls.put("https://fetlife.com/glossary",R.string.title_activity_glossary);
        supportedBaseUrls.put("https://fetlife.com/wallpapers",R.string.title_activity_wallpapers);
        supportedBaseUrls.put("https://fetlife.com/team",R.string.title_activity_team);
        supportedBaseUrls.put("https://fetlife.com/notifications",R.string.title_activity_notifications);
        supportedBaseUrls.put("https://fetlife.com/requests",R.string.title_activity_friendrequests);
        supportedBaseUrls.put("https://fetlife.com/help",R.string.title_activity_help);
        supportedBaseUrls.put("https://fetlife.com/guidelines",R.string.title_activity_guidelines);
        supportedBaseUrls.put("https://fetlife.com/contact",R.string.title_activity_contact);
        supportedBaseUrls.put("https://fetlife.com/android",R.string.title_activity_about);
        supportedBaseUrls.put("https://fetlife.com/privacy",R.string.title_activity_privacy);
        supportedBaseUrls.put("https://fetlife.com/legalese/tou",R.string.title_activity_terms);
        supportedBaseUrls.put("https://fetlife.com/legalese/legal_requests",R.string.title_activity_legal);
        supportedBaseUrls.put("https://fetlife.com/legalese/2257exempt",R.string.title_activity_2257exempt);
        supportedBaseUrls.put(FAB_LINK_NEW_QUESTION,R.string.title_activity_new_question);
    }

    private static final String EXTRA_PAGE_URL = "EXTRA_PAGE_URL";
    private static final String EXTRA_PAGE_TITLE = "EXTRA_PAGE_TITLE";

    private TurbolinksView turbolinksView;
    private Set<String> requestedMediaIds = new HashSet<>();

    private String title;
    private String baseLocation = null;
    private String currentLocation = null;
    private boolean hasBottomBar = true;

    private Consumer actionCableConsumer;
    private boolean clearHistory = false;

    public static void startActivity(BaseActivity menuActivity, String pageUrl, String title) {
        menuActivity.startActivity(createIntent(menuActivity,pageUrl,title, true, null, false));
    }

    public static void startActivity(Context context, String pageUrl, String title, boolean hasBottomBar, Integer bottomNavId, Bundle options, boolean newTask) {
        Intent intent = createIntent(context, pageUrl, title, hasBottomBar, bottomNavId, newTask);
        context.startActivity(intent,options);
    }

    public static Intent createIntent(Context context, String pageUrl, String title, boolean hasBottomBar, Integer bottomNavId, boolean newTask) {
        Intent intent = new Intent(context,TurboLinksViewActivity.class);
        intent.putExtra(EXTRA_PAGE_URL, pageUrl);
        intent.putExtra(EXTRA_PAGE_TITLE, title);
        intent.putExtra(EXTRA_HAS_BOTTOM_BAR, hasBottomBar);
        if (bottomNavId != null) {
            intent.putExtra(BaseActivity.EXTRA_SELECTED_BOTTOM_NAV_ITEM,bottomNavId);
        }
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        return intent;
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

        String pageUrlParam = getIntent().getStringExtra(EXTRA_PAGE_URL);
        hasBottomBar = getIntent().getBooleanExtra(EXTRA_HAS_BOTTOM_BAR,true);

        NotificationParser.Companion.clearNotificationTypeForUrl(pageUrlParam);

        title = getIntent().getStringExtra(EXTRA_PAGE_TITLE);
        if (title == null) {
            Integer titleId = supportedBaseUrls.get(pageUrlParam);
            if (titleId != null) {
                title = getString(titleId);
            }
        }

        if (!hasBottomBar) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setTitle(title != null ? title : " ");

        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        if (pageUrlParam == null) {
            baseLocation = "about:blank";
        } else if (pageUrlParam.startsWith("https://")) {
            baseLocation = pageUrlParam;
        } else {
            baseLocation = FetLifeService.WEBVIEW_BASE_URL + "/" + pageUrlParam;
        }

        currentLocation = baseLocation;

        Uri baseUri = Uri.parse(baseLocation);
        List<String> pathSegments = baseUri.getPathSegments();
        if (pathSegments.size() > 0 && "settings".equals(pathSegments.get(0))) {
            baseLocation = "https://app.fetlife.com/settings";
        }

        setUpFloatingActionButton(getFabLinkForLocation(currentLocation));

        if (BuildConfig.DEBUG) {
            Log.d("TBLocation",currentLocation);
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
                        .setPullToRefreshEnabled(true)
                        .adapter(TurboLinksViewActivity.this)
                        .view(turbolinksView)
                        .addProgressObserver(TurboLinksViewActivity.this)
                        .addPageObserver(TurboLinksViewActivity.this)
                        .restoreWithCachedSnapshot(false)
                        .visitWithAuthHeader(currentLocation, FetLifeService.AUTH_HEADER_PREFIX + accessToken);

                getFetLifeApplication().getActionCable().tryConnect(TurboLinksViewActivity.this);

            }
        },33);
    }

    @Override
    protected String getFabLink() {
        return getFabLinkForLocation(currentLocation);
    }

    private String getFabLinkForLocation(String location) {
        if (location == null) {
            return null;
        }
        Uri baseUri = Uri.parse(location);
        if (!baseUri.isHierarchical()) {
            return null;
        }
        List<String> pathSegments = baseUri.getPathSegments();
        if (pathSegments.size() != 1) {
            return null;
        }
        return "q".equalsIgnoreCase(pathSegments.get(0)) ? FAB_LINK_NEW_QUESTION : null;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String location) {

        if (location == null) {
            return true;
        }

        String mediaId = null;
        Uri uri = Uri.parse(location);
        if (!location.startsWith(FetLifeService.WEBVIEW_BASE_URL)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        } else if ((mediaId = UrlUtil.isMediaRequested(this,Uri.parse(location))) != null){
            requestedMediaIds.add(mediaId);
            return true;
        } else {
            if (baseLocation.equalsIgnoreCase(location)) {
                clearHistory = true;
                return false;
            }

            if (UrlUtil.handleInternal(this,uri, location.startsWith(baseLocation), baseLocation)) {
                return true;
            }
        }
        visitProposedToLocationWithAction(location, "advance");
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (actionCableConsumer != null) {
            actionCableConsumer.disconnect();
            actionCableConsumer = null;
        }
    }

    @Override
    public void onPageFinished() {
        hideProgress(true);
    }

    @Override
    public void onReceivedError(int errorCode) {
        hideProgress(false);
        if (!isFinishing()) {
            showToast(getString(R.string.error_apicall_failed));
            if (!hasBottomBar) {
                finish();
            } else if (!"about:blank".equalsIgnoreCase(TurbolinksSession.getDefault(this).getWebView().getUrl())){
//                TurbolinksSession.getDefault(this).getWebView().setVisibility(View.INVISIBLE);
                currentLocation = "about:blank";
                setUpFloatingActionButton(currentLocation);
                TurbolinksSession.getDefault(this).getWebView().loadUrl(currentLocation);
            }
        }
    }

    @Override
    public void pageInvalidated() {

    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {
        hideProgress(false);
        if (!isFinishing()) {
            if (statusCode == 401) {
                LoginActivity.startLogin(getFetLifeApplication());
                showToast(getString(R.string.error_authentication_failed));
            } else {
                showToast(getString(R.string.error_apicall_failed));
                if (!hasBottomBar) {
                    finish();
                } else if (!"about:blank".equalsIgnoreCase(TurbolinksSession.getDefault(this).getWebView().getUrl())){
                    currentLocation = "about:blank";
                    setUpFloatingActionButton(currentLocation);
                    TurbolinksSession.getDefault(this).getWebView().loadUrl(currentLocation);
//                    TurbolinksSession.getDefault(this).getWebView().setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void visitCompleted() {
        //TurbolinksSession.getDefault(TurboLinksViewActivity.this).getWebView().setVisibility(View.VISIBLE);
        hideProgress(true);
        if (clearHistory) {
            clearHistory = false;
            TurbolinksSession.getDefault(this).getWebView().clearHistory();
        }
        if (isContentNotificationRelated() && !getFetLifeApplication().getActionCable().isConnected()) {
            FetLifeApiIntentService.startApiCall(this,FetLifeApiIntentService.ACTION_APICALL_NOTIFICATION_COUNTS);
        }
        getFetLifeApplication().getActionCable().tryConnect(this);
        if (TextUtils.isEmpty(title)) {
            title = TurbolinksSession.getDefault(this).getWebView().getTitle();
            int separatorPos = title.indexOf(WEB_TITLE_SEPARATOR);
            if (separatorPos >= 0) {
                title = title.substring(0,separatorPos);
            }
            if (!TextUtils.isEmpty(title)) {
                setTitle(title);
            }
        }
    }

    private String getAccessToken() {
        Member currentUser = getFetLifeApplication().getUserSessionManager().getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return currentUser.getAccessToken();
    }

    private boolean isContentNotificationRelated() {
        if (baseLocation != null && baseLocation.startsWith("https://fetlife.com/notifications")) {
            return true;
        } else if (baseLocation != null && baseLocation.startsWith("https://fetlife.com/requests")) {
            return true;
        } else if (baseLocation != null && baseLocation.startsWith("https://fetlife.com/inbox")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        if (BuildConfig.DEBUG) {
            Log.d("TBLocation",location);
        }

        clearHistory = shouldClearHistory(location);

//        if (baseLocation.equalsIgnoreCase(location)) {
//            return;
//        }

        String mediaId = null;

        if (!location.startsWith(baseLocation) && !browsingThrough(baseLocation,location)) {
            Integer expectedTitleResourceId = getTitleForSupportedLocation(location);
            if (expectedTitleResourceId != null) {
                TurboLinksViewActivity.startActivity(this,location,getString(expectedTitleResourceId));
                return;
            } else if ((mediaId = UrlUtil.isMediaRequested(this,Uri.parse(location))) != null) {
                requestedMediaIds.add(mediaId);
                return;
            } else if (UrlUtil.handleInternal(this,Uri.parse(location), false, baseLocation)){
                return;
            } else {
                UrlUtil.openUrl(this,UrlUtil.removeAppIds(location), true, false);
                return;
            }
        } else if (UrlUtil.handleInternal(this,Uri.parse(location), true, baseLocation)){
            return;
        } else {
            location = UrlUtil.removeAppIds(location);
            Integer expectedTitleResourceId = getTitleForSupportedLocation(location);
            if (expectedTitleResourceId != null) {
                setTitle(expectedTitleResourceId);
            } else {
                title = null;
            }
        }

        TurbolinksView turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        currentLocation = location;
        setUpFloatingActionButton(getFabLink());

        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .restoreWithCachedSnapshot(false)
                .visitLocationWithAction(location,action);
    }

    private boolean browsingThrough(String baseLocation, String location) {
        List<String> uriSegments = Uri.parse(location).getPathSegments();
        List<String> currentUriSegments = Uri.parse(currentLocation).getPathSegments();
        if (uriSegments.size() == 0 || currentUriSegments.size() == 0) {
            return false;
        }
        if ("places".equals(uriSegments.get(0)) || "administrative_areas".equals(uriSegments.get(0)) && ("places".equals(currentUriSegments.get(0)) || "administrative_areas".equals(currentUriSegments.get(0)))) {
            return true;
        }
        if ("users".equals(uriSegments.get(0)) && "users".equals(currentUriSegments.get(0))) {
            if (uriSegments.size() < 3 || currentUriSegments.size() < 3) {
                return false;
            }
            if ("posts".equals(uriSegments.get(2)) && "posts".equals(currentUriSegments.get(2))) {
                return true;
            }
        }
        return false;
    }

    private boolean shouldClearHistory(String location) {
        List<String> uriSegments = Uri.parse(location).getPathSegments();
        List<String> currentUriSegments = Uri.parse(currentLocation).getPathSegments();
        if (uriSegments.size() == 0 || currentUriSegments.size() == 0) {
            return false;
        }
        if ("settings".equals(uriSegments.get(0)) && "settings".equals(currentUriSegments.get(0))) {
            if (currentUriSegments.size() == 1 || !uriSegments.get(1).equals(currentUriSegments.get(1))) {
                return true;
            }
        }
        if ("places".equals(uriSegments.get(0)) && "settings".equals(currentUriSegments.get(0))) {
            return true;
        }
        return false;
    }

    private Integer getTitleForSupportedLocation(String location) {
        location = location.replace("//app.fetlife.com","//fetlife.com");
        if (location.startsWith(URL_QUESTIONS_POPULAR)) {
            return R.string.title_webview_questions_popular;
        }
        if (location.startsWith(URL_QUESTIONS_TAGS)) {
            return R.string.title_webview_questions_tags;
        }
        if (location.startsWith(URL_QUESTIONS)) {
            return R.string.title_activity_questions;
        }
        for (Map.Entry<String,Integer> entry : supportedBaseUrls.entrySet()) {
            if (location.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
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
    public void onBackPressed() {
        WebView webview = TurbolinksSession.getDefault(this).getWebView();;
        if (webview.canGoBack()) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "Back Pressed", Toast.LENGTH_SHORT).show();
        }
            webview.goBack();
        } else {
            super.onBackPressed();
        }
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
                UrlUtil.openUrl(TurboLinksViewActivity.this,url, true, false);
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
        List<Picture> pictures = new ArrayList<>();
        pictures.add(picture);
        getFetLifeApplication().getImageViewerWrapper().show(this,pictures,0);
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