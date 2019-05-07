package com.bitlove.fetlife.legacy.view.screen.resource.groups;

import android.content.Context;
import android.content.Intent;
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
import androidx.viewpager.widget.ViewPager;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.legacy.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.legacy.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.legacy.util.UrlUtil;
import com.bitlove.fetlife.legacy.view.dialog.ConfirmationDialog;
import com.bitlove.fetlife.common.view.ResourceActivity;
import com.bitlove.fetlife.legacy.view.widget.FlingBehavior;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.appbar.AppBarLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class GroupActivity extends ResourceActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final String EXTRA_GROUPID = "EXTRA_GROUPID";
    private static final String EXTRA_GROUP_TITLE = "EXTRA_GROUP_TITLE";
    private String groupId;

    private TextView groupSubTitle;
    private TextView groupTitle;
    private ImageView membershipIcon;
    private TextView membershipIconText;

    private ViewPager viewPager;
    private Group group;

    public static Intent createIntent(Context context, String groupId, String groupTitle, boolean newTask) {
        Intent intent = new Intent(context, GroupActivity.class);
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        intent.putExtra(EXTRA_GROUPID, groupId);
        intent.putExtra(EXTRA_GROUP_TITLE, groupTitle);
        return intent;
    }

    public static void startActivity(Context Context, String groupId, String groupTitle, boolean newTask) {
        Context.startActivity(createIntent(Context,groupId,groupTitle,newTask));
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        membershipIcon = (ImageView) findViewById(R.id.group_menu_icon_member);
        membershipIconText = (TextView) findViewById(R.id.group_menu_icon_text_member);

        group = Group.loadGroup(getIntent().getStringExtra(EXTRA_GROUPID));
        groupId = group != null ? group.getId() : getIntent().getStringExtra(EXTRA_GROUPID);

        String groupTitle = getIntent().getStringExtra(EXTRA_GROUP_TITLE);
        if (group != null) {
            setGroupDetails(group);
        } else if (groupId != null){
            if (groupTitle != null){
                setGroupDetails(groupTitle,-1);
            }
            FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_GROUP, groupId);
        } else {
            Crashlytics.logException(new Exception("groupId must not be null"));
        }
        findViewById(R.id.group_menu_icon_view_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewGroup(v);
            }
        });

        findViewById(R.id.group_menu_icon_member_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMembershipIcon(v);
            }
        });

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 1:
                        return GroupInfoFragment.newInstance(groupId, GroupInfoFragment.GroupInfoEnum.DESCRIPTION);
                    case 2:
                        return GroupInfoFragment.newInstance(groupId, GroupInfoFragment.GroupInfoEnum.RULES);
                    case 0:
                        return GroupDiscussionsFragment.newInstance(groupId);
                    case 3:
                        return GroupMembersFragment.newInstance(groupId);
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 1:
                        return getString(R.string.title_fragment_group_description);
                    case 2:
                        return getString(R.string.title_fragment_group_rules);
                    case 0:
                        return getString(R.string.title_fragment_group_discussions);
                    case 3:
                        return getString(R.string.title_fragment_group_members);
                    default:
                        return null;
                }
            }
        });

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        params.setBehavior(new FlingBehavior());
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("");
        getSupportActionBar().setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        TextView headerTitle = (TextView) findViewById(R.id.group_title);
        toolbarTitle.setText(title);
        headerTitle.setText(title);
    }

    private void setGroupDetails(Group group) {
        this.group = group;
        this.groupId = group.getId();
        setGroupDetails(group.getName(),group.getMemberCount());
        if (group.isDetailLoaded()) {
            membershipIcon.setVisibility(View.VISIBLE);
            membershipIconText.setVisibility(View.VISIBLE);
            if (group.isMemberOfGroup()) {
                membershipIcon.setImageResource(R.drawable.ic_group_member_of_24dp);
                membershipIconText.setText(R.string.menu_group_member_leave);
                membershipIcon.setColorFilter(getResources().getColor(R.color.text_color_primary));
            } else {
                membershipIcon.setColorFilter(getResources().getColor(R.color.text_color_secondary));
                membershipIconText.setText(R.string.menu_group_member_join);
                membershipIcon.setImageResource(R.drawable.ic_group_request_sent_24dp);
                membershipIcon.setImageResource(R.drawable.ic_group_join_black_24dp);
            }
        }
    }

    private void setGroupDetails(String groupTitle, int memberCount) {
        if (groupTitle != null) {
            setTitle(groupTitle);
        }
        groupSubTitle = (TextView) findViewById(R.id.group_subtitle);
        if (memberCount > -1) {
            groupSubTitle.setText(getString(memberCount == 1 ? R.string.text_group_member_count : R.string.text_group_members_count,memberCount));
        } else {
            groupSubTitle.setText("");
        }
        membershipIcon.setVisibility(View.INVISIBLE);
        membershipIconText.setVisibility(View.INVISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallStarted(ServiceCallStartedEvent serviceCallStartedGroup) {
        if (isRelatedCall(serviceCallStartedGroup.getServiceCallAction(), serviceCallStartedGroup.getParams())) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFinished(ServiceCallFinishedEvent serviceCallFinishedGroup) {
        if (isRelatedCall(serviceCallFinishedGroup.getServiceCallAction(), serviceCallFinishedGroup.getParams())) {
            Group group = Group.loadGroup(groupId);
            setGroupDetails(group);
            if (!isRelatedCall(FetLifeApiIntentService.getActionInProgress(), FetLifeApiIntentService.getInProgressActionParams())) {
                dismissProgress();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFailed(ServiceCallFailedEvent serviceCallFailedGroup) {
        if (isRelatedCall(serviceCallFailedGroup.getServiceCallAction(), serviceCallFailedGroup.getParams())) {
            dismissProgress();
        }
    }

    private boolean isRelatedCall(String serviceCallAction, String[] params) {
        if (params != null && params.length > 0 && groupId != null && !groupId.equals(params[0])) {
            return false;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_GROUP_JOIN.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_GROUP_LEAVE.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_GROUP.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_GROUP_DISCUSSIONS.equals(serviceCallAction)) {
            return true;
        }
        return false;
    }

    public void onViewGroup(View v) {
        if (group != null) {
            UrlUtil.openUrl(this,group.getUrl(), true, false);
        }
    }

    public void onMembershipIcon(View v) {
        if (group == null) {
            return;
        }
        if (!group.isMemberOfGroup()) {
            ConfirmationDialog groupConfirmationDialog = ConfirmationDialog.newInstance(getString(R.string.title_dialog_join_group),getString(R.string.message_dialog_join_group));
            groupConfirmationDialog.setRightButton(getString(R.string.button_dialog_yes), new ConfirmationDialog.OnClickListener(){
                @Override
                public void onClick(ConfirmationDialog groupConfirmationDialog) {
                    group.setMemberOfGroup(true);
                    group.save();
                    FetLifeApiIntentService.startApiCall(GroupActivity.this,FetLifeApiIntentService.ACTION_APICALL_GROUP_JOIN,group.getId());
                    groupConfirmationDialog.dismissAllowingStateLoss();
                    setGroupDetails(group);
                }
            });
            groupConfirmationDialog.setLeftButton(getString(R.string.button_dialog_no), new ConfirmationDialog.OnClickListener(){
                @Override
                public void onClick(ConfirmationDialog groupConfirmationDialog) {
                    groupConfirmationDialog.dismissAllowingStateLoss();
                }
            });
            groupConfirmationDialog.show(getFragmentManager());
        } else {
            ConfirmationDialog groupConfirmationDialog = ConfirmationDialog.newInstance(getString(R.string.title_dialog_leave_group),getString(R.string.message_dialog_leave_group));
            groupConfirmationDialog.setRightButton(getString(R.string.button_dialog_yes), new ConfirmationDialog.OnClickListener(){
                @Override
                public void onClick(ConfirmationDialog groupConfirmationDialog) {
                    group.setMemberOfGroup(false);
                    group.save();
                    FetLifeApiIntentService.startApiCall(GroupActivity.this,FetLifeApiIntentService.ACTION_APICALL_GROUP_LEAVE,group.getId());
                    groupConfirmationDialog.dismissAllowingStateLoss();
                    setGroupDetails(group);
                }
            });
            groupConfirmationDialog.setLeftButton(getString(R.string.button_dialog_no), new ConfirmationDialog.OnClickListener(){
                @Override
                public void onClick(ConfirmationDialog groupConfirmationDialog) {
                    groupConfirmationDialog.dismissAllowingStateLoss();
                }
            });
            groupConfirmationDialog.show(getFragmentManager());
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreateActivityComponents() {
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_group);
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
