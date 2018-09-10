package com.bitlove.fetlife.view.screen.resource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.FriendRequestResponseSendFailedEvent;
import com.bitlove.fetlife.event.FriendRequestResponseSendSucceededEvent;
import com.bitlove.fetlife.event.FriendSuggestionAddedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.db.SharedProfile;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.FriendRequest;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.notification.AnonymNotification;
import com.bitlove.fetlife.notification.FriendRequestNotification;
import com.bitlove.fetlife.view.adapter.FriendRequestsRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.view.screen.component.MenuActivityComponent;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FriendRequestsActivity extends ResourceListActivity<BaseModel> {

    public static void startActivity(Context context, boolean newTask) {
        context.startActivity(createIntent(context, newTask));
    }

    public static Intent createIntent(Context context, boolean newTask) {
        Intent intent = new Intent(context, FriendRequestsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    @Override
    protected void onCreateActivityComponents() {
        addActivityComponent(new MenuActivityComponent());
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {
        super.onResourceCreate(savedInstanceState);
        showToast(getResources().getString(R.string.friendrequest_activity_hint));
    }

    @Override
    protected void onResourceStart() {
        super.onResourceStart();
        FriendRequestNotification.clearNotifications();
        AnonymNotification.clearNotifications();
    }

    @Override
    protected String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_FRIENDREQUESTS;
    }

    @Override
    protected ResourceListRecyclerAdapter createRecyclerAdapter(Bundle savedInstanceState) {
        return new FriendRequestsRecyclerAdapter(savedInstanceState == null);
    }

    @Override
    public void onItemClick(BaseModel friendRequestScreenItem) {

    }

    @Override
    public void onAvatarClick(BaseModel friendRequestScreenItem) {
        if (friendRequestScreenItem instanceof FriendRequest) {
            ProfileActivity.startActivity(this,((FriendRequest)friendRequestScreenItem).getMemberId());
        } else {
            ProfileActivity.startActivity(this,((SharedProfile)friendRequestScreenItem).getMemberId());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendRequestDecisionSent(FriendRequestResponseSendSucceededEvent friendRequestResponseSendSucceededEvent) {
        recyclerAdapter.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendRequestDecisionSendFailed(FriendRequestResponseSendFailedEvent friendRequestSendFailedEvent) {
        recyclerAdapter.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendSuggestionAdded(FriendSuggestionAddedEvent friendSuggestionAddedEvent) {
        recyclerAdapter.refresh();
    }

}
