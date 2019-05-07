package com.bitlove.fetlife.legacy.view.screen.resource.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.legacy.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.legacy.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.db.FollowRequest;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.db.RelationReference;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.FriendRequest;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.legacy.util.ViewUtil;
import com.bitlove.fetlife.legacy.view.dialog.ConfirmationDialog;
import com.bitlove.fetlife.common.view.ResourceActivity;
import com.bitlove.fetlife.legacy.view.widget.FlingBehavior;
import com.bitlove.fetlife.legacy.view.widget.SlideControlViewPager;
import com.bitlove.fetlife.legacy.view.widget.SlidingTabLayout;
import com.bitlove.fetlife.webapp.navigation.WebAppNavigation;
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.appbar.AppBarLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ProfileActivity extends ResourceActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final int PROFILE_MENU_HITREC_PADDING = 200;

    private static final String EXTRA_MEMBERID = "EXTRA_MEMBERID";
    private static final String EXTRA_TOAST_MESSAGE = "EXTRA_TOAST_MESSAGE";

    private static final int PAGE_NUMBER_PICTURES = 4;
    private static final String MENMTION_PREFEIX = "@";

    private SlideControlViewPager viewPager;
    private TextView nickNameView,metaView;
    private SimpleDraweeView avatarView,imageHeaderView,toolbarHeaderView;
    private ImageView friendIconView,followIconView,messageIconView,viewIconView;
    private TextView friendIconTextView,followIconTextView,messageIconTextView;
    private String memberId;

    public static void startActivity(Context context, String memberId) {
        startActivity(context, memberId, false, null);
    }

    public static void startActivity(Context context, String memberId, boolean clearTop, String toastMessage) {
        if (context == null) {
            return;
        }
        context.startActivity(createIntent(context,memberId, clearTop,toastMessage));
    }

    public static Intent createIntent(Context context, String memberId, boolean clearTop, String toastMessage) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (clearTop) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        intent.putExtra(EXTRA_MEMBERID,memberId);
        intent.putExtra(EXTRA_TOAST_MESSAGE,toastMessage);
        return intent;
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        displayIntentMessage();

        Member member = Member.loadMember(getIntent().getStringExtra(EXTRA_MEMBERID));
        memberId = member != null ? member.getId() : getIntent().getStringExtra(EXTRA_MEMBERID);

        nickNameView = (TextView) findViewById(R.id.profile_nickname);
        metaView = (TextView) findViewById(R.id.profile_meta);
        avatarView = (SimpleDraweeView) findViewById(R.id.profile_avatar);
        imageHeaderView = (SimpleDraweeView) findViewById(R.id.profile_image_header);
        toolbarHeaderView = (SimpleDraweeView) findViewById(R.id.toolbar_image);
        friendIconView = (ImageView) findViewById(R.id.profile_menu_icon_friend);
        followIconView = (ImageView) findViewById(R.id.profile_menu_icon_follow);
        messageIconView = (ImageView) findViewById(R.id.profile_menu_icon_message);
        friendIconTextView = (TextView) findViewById(R.id.profile_menu_text_friend);
        followIconTextView = (TextView) findViewById(R.id.profile_menu_text_follow);
        messageIconTextView = (TextView) findViewById(R.id.profile_menu_text_message);
        viewIconView = (ImageView) findViewById(R.id.profile_menu_icon_view);

        setMemberDetails(member);

        viewPager = (SlideControlViewPager) findViewById(R.id.pager);
        viewPager.setSlideEnabled(true);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return BasicInfoFragment.newInstance(memberId);
                    case 1:
                        return AboutFragment.newInstance(memberId);
                    case 2:
                        return StatusesFragment.newInstance(memberId);
                    case 3:
                        return ActivityFeedFragment.newInstance(memberId);
                    case PAGE_NUMBER_PICTURES:
                        return PicturesFragment.newInstance(memberId);
                    case 5:
                        return VideosFragment.newInstance(memberId);
                    case 6:
                        return WritingsFragment.newInstance(memberId);
                    case 7:
                        return EventsFragment.newInstance(memberId);
                    case 8:
                        return GroupsFragment.newInstance(memberId);
                    case 9:
                        return RelationsFragment.newInstance(memberId,RelationReference.VALUE_RELATIONTYPE_FRIEND);
                    case 10:
                        return RelationsFragment.newInstance(memberId,RelationReference.VALUE_RELATIONTYPE_FOLLOWING);
                    case 11:
                        return RelationsFragment.newInstance(memberId,RelationReference.VALUE_RELATIONTYPE_FOLLOWER);
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 12;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getString(R.string.title_fragment_profile_info);
                    case 1:
                        return getString(R.string.title_fragment_profile_about);
                    case 2:
                        return getString(R.string.title_fragment_profile_statuses);
                    case 3:
                        return getString(R.string.title_fragment_profile_feed);
                    case PAGE_NUMBER_PICTURES:
                        return getString(R.string.title_fragment_profile_pictures);
                    case 5:
                        return getString(R.string.title_fragment_profile_videos);
                    case 6:
                        return getString(R.string.title_fragment_profile_writings);
                    case 7:
                        return getString(R.string.title_fragment_profile_events);
                    case 8:
                        return getString(R.string.title_fragment_profile_groups);
                    case 9:
                        return getString(R.string.title_fragment_profile_friends);
                    case 10:
                        return getString(R.string.title_fragment_profile_following);
                    case 11:
                        return getString(R.string.title_fragment_profile_followers);
                    default:
                        return null;
                }
            }
        });

        SlidingTabLayout slidingTab = (SlidingTabLayout) findViewById(R.id.navigation_tabs);
        slidingTab.setDividerColorResource(R.color.area_background_dark_invert);
        slidingTab.setSelectedIndicatorColorResource(R.color.color_accent);
        slidingTab.setViewPager(viewPager);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        params.setBehavior(new FlingBehavior());
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Member member = Member.loadMember(getIntent().getStringExtra(EXTRA_MEMBERID));
        memberId = member != null ? member.getId() : getIntent().getStringExtra(EXTRA_MEMBERID);

        displayIntentMessage();
        setMemberDetails(member);
    }

    private void displayIntentMessage() {
        String toastMessage = getIntent().getStringExtra(EXTRA_TOAST_MESSAGE);
        if (toastMessage != null) {
            showToast(toastMessage);
            Intent intent = getIntent();
            intent.removeExtra(EXTRA_TOAST_MESSAGE);
            setIntent(intent);
        }
    }

    private void setMemberDetails(final Member member) {
        if (member == null) {
            Crashlytics.logException(new Exception("Member is null"));
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (member != null) {
                    member.setLastViewedAt(System.currentTimeMillis());
                    member.mergeSave();
                }
            }
        });

        Member currentUser = getFetLifeApplication().getUserSessionManager().getCurrentUser();
        boolean sameUser = member != null && currentUser != null && member.getId().equals(currentUser.getId());

        setTitle(member != null ? member.getNickname() : "");
        nickNameView.setText(member != null ? member.getNickname() : "");
        metaView.setText(member != null ? member.getMetaInfo() : "");
        avatarView.setImageURI(member != null ? member.getAvatarLink() : "");
        imageHeaderView.setImageURI(member != null ? member.getAvatarLink() : "");
        imageHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(PAGE_NUMBER_PICTURES);
            }
        });
        toolbarHeaderView.setImageURI(member != null ? member.getAvatarLink() : "");
        toolbarHeaderView.setTag(member != null ? member.getAvatarLink() : "");

        String relationWithMe = member != null ? member.getRelationWithMe() : "";
        if (relationWithMe == null) {
            relationWithMe = "";
        }
        switch (relationWithMe) {
            case Member.VALUE_FRIEND:
            case Member.VALUE_FRIEND_WITHOUT_FOLLOWING:
                friendIconView.setImageResource(R.drawable.ic_friend);
                break;
            case Member.VALUE_FOLLOWING_FRIEND_REQUEST_SENT:
            case Member.VALUE_FRIEND_REQUEST_SENT:
                friendIconView.setImageResource(R.drawable.ic_friend_sent);
                break;
            case Member.VALUE_FOLLOWING_FRIEND_REQUEST_PENDING:
            case Member.VALUE_FRIEND_REQUEST_PENDING:
                friendIconView.setImageResource(R.drawable.ic_friend_pending);
                break;
            default:
                friendIconView.setImageResource(R.drawable.ic_friend_add);
                break;
        }
        friendIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuIconFriend();
            }
        });
        friendIconView.setVisibility(sameUser || member == null || !member.isDetailRetrieved() ? View.INVISIBLE : View.VISIBLE);
        friendIconTextView.setVisibility(sameUser || member == null || !member.isDetailRetrieved()? View.INVISIBLE : View.VISIBLE);
        ViewUtil.increaseTouchArea(friendIconView,PROFILE_MENU_HITREC_PADDING);
        followIconView.setImageResource(isFollowedByMe(member) ? R.drawable.ic_following : R.drawable.ic_follow);
        followIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuIconFollow();
            }
        });
        followIconView.setVisibility(sameUser || member == null || !member.isDetailRetrieved() || !member.isFollowable() ? View.INVISIBLE : View.VISIBLE);
        followIconTextView.setVisibility(sameUser || member == null || !member.isDetailRetrieved() || !member.isFollowable() ? View.INVISIBLE : View.VISIBLE);
        ViewUtil.increaseTouchArea(followIconView,PROFILE_MENU_HITREC_PADDING);
        messageIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuIconMessage();
            }
        });
        messageIconView.setVisibility(sameUser ? View.INVISIBLE : View.VISIBLE);
        messageIconTextView.setVisibility(sameUser ? View.INVISIBLE : View.VISIBLE);
        ViewUtil.increaseTouchArea(messageIconView,PROFILE_MENU_HITREC_PADDING);
        viewIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuIconView();
            }
        });
        viewIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuIconView();
            }
        });
        ViewUtil.increaseTouchArea(viewIconView,PROFILE_MENU_HITREC_PADDING);
    }

    private boolean isFollowedByMe(Member member) {
        if (member == null) {
            return false;
        }
        String relationWithMe = member.getRelationWithMe();
        if (relationWithMe == null) {
            return false;
        }
        return relationWithMe.equals(Member.VALUE_FRIEND) || relationWithMe.equals(Member.VALUE_FOLLOWING) ||  relationWithMe.equals(Member.VALUE_FOLLOWING_FRIEND_REQUEST_SENT) ||  relationWithMe.equals(Member.VALUE_FOLLOWING_FRIEND_REQUEST_PENDING) ;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (isRelatedCall(serviceCallStartedEvent.getServiceCallAction(), serviceCallStartedEvent.getParams())) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction().equals(FetLifeApiIntentService.ACTION_APICALL_MEMBER)) {
            Member member = Member.loadMember(memberId);
            if (member != null) {
                setMemberDetails(member);
            }
        }
        if (isRelatedCall(serviceCallFinishedEvent.getServiceCallAction(),serviceCallFinishedEvent.getParams()) && !isRelatedCall(FetLifeApiIntentService.getActionInProgress(),FetLifeApiIntentService.getInProgressActionParams())) {
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (isRelatedCall(serviceCallFailedEvent.getServiceCallAction(), serviceCallFailedEvent.getParams())) {
            dismissProgress();
        }
    }

    private boolean isRelatedCall(String serviceCallAction, String[] params) {
        if (params != null && params.length > 0 && memberId != null && !memberId.equals(params[0])) {
            return false;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_FEED.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_STATUSES.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_PICTURES.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_RELATIONS.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_VIDEOS.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_EVENTS.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_GROUPS.equals(serviceCallAction)) {
            return true;
        }
        return false;
    }

    private void onMenuIconMessage() {
        Member member = Member.loadMember(memberId);
        if (member != null) {
            FetLifeWebViewActivity.Companion.startActivity(this,"conversations/new?with="+member.getServerId(),false,null, false, null, null);
        }
    }

    private void onMenuIconView() {
        Member member = Member.loadMember(memberId);
        if (member != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(member.getLink()));
            startActivity(intent);
        }
    }

    private void onMenuIconFriend() {
        final Member member = Member.loadMember(memberId);
        final String currentRelation = member.getRelationWithMe() != null ? member.getRelationWithMe() : "";
        switch (currentRelation) {
            case Member.VALUE_FRIEND:
            case Member.VALUE_FRIEND_WITHOUT_FOLLOWING:
                ConfirmationDialog profileConfirmationDialog = ConfirmationDialog.newInstance(getString(R.string.title_dialog_cancel_friendship),getString(R.string.message_dialog_cancel_friendship));
                profileConfirmationDialog.setRightButton(getString(R.string.button_dialog_yes), new ConfirmationDialog.OnClickListener(){
                    @Override
                    public void onClick(ConfirmationDialog profileConfirmationDialog) {
                        FetLifeApiIntentService.startApiCall(ProfileActivity.this,FetLifeApiIntentService.ACTION_APICALL_CANCEL_FRIENDSHIP,member.getId());
                        member.setRelationWithMe(null);
                        member.mergeSave();
                        setMemberDetails(member);
                        profileConfirmationDialog.dismissAllowingStateLoss();
                    }
                });
                profileConfirmationDialog.setLeftButton(getString(R.string.button_dialog_no), new ConfirmationDialog.OnClickListener(){
                    @Override
                    public void onClick(ConfirmationDialog profileConfirmationDialog) {
                        profileConfirmationDialog.dismissAllowingStateLoss();
                    }
                });
                profileConfirmationDialog.show(getFragmentManager());
                break;
            case Member.VALUE_FOLLOWING_FRIEND_REQUEST_SENT:
            case Member.VALUE_FRIEND_REQUEST_SENT:
                 profileConfirmationDialog = ConfirmationDialog.newInstance(getString(R.string.title_dialog_cancel_friendrequest),getString(R.string.message_dialog_cancel_friendrequest));
                profileConfirmationDialog.setRightButton(getString(R.string.button_dialog_yes), new ConfirmationDialog.OnClickListener(){
                    @Override
                    public void onClick(ConfirmationDialog profileConfirmationDialog) {
                        FetLifeApiIntentService.startApiCall(ProfileActivity.this,FetLifeApiIntentService.ACTION_APICALL_CANCEL_FRIENDREQUEST,member.getId());
                        member.setRelationWithMe(currentRelation.equals(Member.VALUE_FOLLOWING_FRIEND_REQUEST_SENT) ? Member.VALUE_FOLLOWING : null);
                        member.mergeSave();
                        setMemberDetails(member);
                        profileConfirmationDialog.dismissAllowingStateLoss();
                    }
                });
                profileConfirmationDialog.setLeftButton(getString(R.string.button_dialog_no), new ConfirmationDialog.OnClickListener(){
                    @Override
                    public void onClick(ConfirmationDialog profileConfirmationDialog) {
                        profileConfirmationDialog.dismissAllowingStateLoss();
                    }
                });
                profileConfirmationDialog.show(getFragmentManager());
                break;
            case Member.VALUE_FOLLOWING_FRIEND_REQUEST_PENDING:
            case Member.VALUE_FRIEND_REQUEST_PENDING:
                profileConfirmationDialog = ConfirmationDialog.newInstance(getString(R.string.title_dialog_approve_friendrequest),getString(R.string.message_dialog_approve_friendrequest));
                profileConfirmationDialog.setRightButton(getString(R.string.button_dialog_yes), new ConfirmationDialog.OnClickListener(){
                    @Override
                    public void onClick(ConfirmationDialog profileConfirmationDialog) {
                        FetLifeWebViewActivity.Companion.startActivity(ProfileActivity.this, WebAppNavigation.WEBAPP_BASE_URL + "/requests", true, R.id.navigation_bottom_requests,false, null, null);
                        profileConfirmationDialog.dismissAllowingStateLoss();
                    }
                });
                profileConfirmationDialog.setLeftButton(getString(R.string.button_dialog_no), new ConfirmationDialog.OnClickListener(){
                    @Override
                    public void onClick(ConfirmationDialog profileConfirmationDialog) {
                        profileConfirmationDialog.dismissAllowingStateLoss();
                    }
                });
                profileConfirmationDialog.show(getFragmentManager());
                break;
            default:
                profileConfirmationDialog = ConfirmationDialog.newInstance(getString(R.string.title_dialog_send_friendrequest),getString(R.string.message_dialog_send_friendrequest));
                profileConfirmationDialog.setRightButton(getString(R.string.button_dialog_yes), new ConfirmationDialog.OnClickListener(){
                    @Override
                    public void onClick(ConfirmationDialog profileConfirmationDialog) {
                        FriendRequest friendRequest = new FriendRequest();
                        friendRequest.setClientId(member.getId());
                        friendRequest.setTargetMemberId(member.getId());
                        friendRequest.setPendingState(FriendRequest.PendingState.OUTGOING);
                        friendRequest.setPending(true);
                        friendRequest.save();
                        FetLifeApiIntentService.startApiCall(ProfileActivity.this,FetLifeApiIntentService.ACTION_APICALL_PENDING_RELATIONS);
                        member.setRelationWithMe(isFollowedByMe(member) ? Member.VALUE_FOLLOWING_FRIEND_REQUEST_SENT : Member.VALUE_FRIEND_REQUEST_SENT);
                        member.mergeSave();
                        setMemberDetails(member);
                        showToast(getString(R.string.message_friend_request_sent,member.getNickname()));
                        profileConfirmationDialog.dismissAllowingStateLoss();
                    }
                });
                profileConfirmationDialog.setLeftButton(getString(R.string.button_dialog_no), new ConfirmationDialog.OnClickListener(){
                    @Override
                    public void onClick(ConfirmationDialog profileConfirmationDialog) {
                        profileConfirmationDialog.dismissAllowingStateLoss();
                    }
                });
                profileConfirmationDialog.show(getFragmentManager());
                break;
        }
    }

    private void onMenuIconFollow() {
        Member member = Member.loadMember(memberId);
        if (member != null && !isFollowedByMe(member) && member.isFollowable()) {
            FollowRequest followRequest = new FollowRequest();
            followRequest.setMemberId(member.getId());
            followRequest.save();
            String relationShipWithMe = member.getRelationWithMe() != null ? member.getRelationWithMe() : "";
            switch (relationShipWithMe) {
                case Member.VALUE_FRIEND_WITHOUT_FOLLOWING:
                    member.setRelationWithMe(Member.VALUE_FRIEND);
                    break;
                case Member.VALUE_FRIEND_REQUEST_PENDING:
                    member.setRelationWithMe(Member.VALUE_FOLLOWING_FRIEND_REQUEST_PENDING);
                    break;
                case Member.VALUE_FRIEND_REQUEST_SENT:
                    member.setRelationWithMe(Member.VALUE_FOLLOWING_FRIEND_REQUEST_SENT);
                    break;
                default:
                    member.setRelationWithMe(Member.VALUE_FOLLOWING);
                    break;
            }
            member.mergeSave();
            setMemberDetails(member);
            FetLifeApiIntentService.startApiCall(this,FetLifeApiIntentService.ACTION_APICALL_PENDING_RELATIONS);
            showToast(getString(R.string.message_follow_set,member.getNickname()));
        } else if (isFollowedByMe(member)){
            FollowRequest followRequest = new FollowRequest();
            followRequest.setMemberId(member.getId());
            followRequest.setFollow(false);
            followRequest.save();
            String relationShipWithMe = member.getRelationWithMe() != null ? member.getRelationWithMe() : "";
            switch (relationShipWithMe) {
                case Member.VALUE_FOLLOWING:
                    member.setRelationWithMe(null);
                    break;
                case Member.VALUE_FOLLOWING_FRIEND_REQUEST_PENDING:
                    member.setRelationWithMe(Member.VALUE_FRIEND_REQUEST_PENDING);
                    break;
                case Member.VALUE_FOLLOWING_FRIEND_REQUEST_SENT:
                    member.setRelationWithMe(Member.VALUE_FRIEND_REQUEST_SENT);
                    break;
                case Member.VALUE_FRIEND:
                    member.setRelationWithMe(Member.VALUE_FRIEND_WITHOUT_FOLLOWING);
                    break;
            }
            member.mergeSave();
            setMemberDetails(member);
            FetLifeApiIntentService.startApiCall(this,FetLifeApiIntentService.ACTION_APICALL_PENDING_RELATIONS);
            showToast(getString(R.string.message_unfollow_set,member.getNickname()));
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
    }

//    private void refresh(String memberId) {
//        showProgress();
//        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER, memberId);
//        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER_STATUSES, memberId, Integer.toString(LoadFragment.ITEM_PER_PAGE), "1");
//        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER_PICTURES, memberId, Integer.toString(PicturesFragment.ITEM_PER_PAGE), "1");
//        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER_RELATIONS, memberId, Integer.toString(RelationReference.VALUE_RELATIONTYPE_FRIEND), Integer.toString(LoadFragment.ITEM_PER_PAGE), "1");
//        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER_RELATIONS, memberId, Integer.toString(RelationReference.VALUE_RELATIONTYPE_FOLLOWER), Integer.toString(LoadFragment.ITEM_PER_PAGE), "1");
//        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER_RELATIONS, memberId, Integer.toString(RelationReference.VALUE_RELATIONTYPE_FOLLOWING), Integer.toString(LoadFragment.ITEM_PER_PAGE), "1");
//        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER_VIDEOS, memberId, Integer.toString(VideosFragment.ITEM_PER_PAGE), "1");
//    }

    @Override
    protected void onStop() {
        super.onStop();
        getFetLifeApplication().getInMemoryStorage().clearProfileFeed();
    }

    @Override
    protected void onCreateActivityComponents() {
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_profile);
    }

    private static final float PERCENTAGE_TO_SHOW_TITLE_DETAILS = 0.8f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private static final long ALPHA_ANIMATIONS_DELAY = 400l;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        setToolbarVisibility(appBarLayout, findViewById(R.id.toolbar_title), findViewById(R.id.toolbar_image), percentage);
    }

    private boolean isTitleVisible = false;

    private void setToolbarVisibility(AppBarLayout appBarLayout, View title, View image, float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_DETAILS) {
            if(!isTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.VISIBLE);
                startAlphaAnimation(image, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.VISIBLE);
                ((SimpleDraweeView)image).setImageURI((String)image.getTag());
                isTitleVisible = true;
            }
        } else {
            if (isTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.INVISIBLE);
                startAlphaAnimation(image, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.INVISIBLE);
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
