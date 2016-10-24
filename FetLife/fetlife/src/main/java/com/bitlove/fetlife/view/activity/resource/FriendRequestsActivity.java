package com.bitlove.fetlife.view.activity.resource;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.FriendRequestSendFailedEvent;
import com.bitlove.fetlife.event.FriendRequestSendSucceededEvent;
import com.bitlove.fetlife.event.FriendSuggestionAddedEvent;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.FriendRequest;
import com.bitlove.fetlife.model.pojos.SharedProfile;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.activity.component.MenuActivityComponent;
import com.bitlove.fetlife.view.adapter.FriendRequestsRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FriendRequestsActivity extends ResourceListActivity<BaseModel> implements MenuActivityComponent.MenuActivityCallBack {

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
    protected String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_FRIENDREQUESTS;
    }

    @Override
    protected ResourceListRecyclerAdapter createRecyclerAdapter(Bundle savedInstanceState) {
        return new FriendRequestsRecyclerAdapter(getFetLifeApplication().getImageLoader(), savedInstanceState == null);
    }

    @Override
    public void onItemClick(BaseModel friendRequestScreenItem) {

    }

    @Override
    public void onAvatarClick(BaseModel friendRequestScreenItem) {
        String url = friendRequestScreenItem instanceof FriendRequest ? ((FriendRequest)friendRequestScreenItem).getMemberLink() : ((SharedProfile)friendRequestScreenItem).getLink();
        if (url != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendRequestDecisionSent(FriendRequestSendSucceededEvent friendRequestSendSucceededEvent) {
        recyclerAdapter.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendRequestDecisionSendFailed(FriendRequestSendFailedEvent friendRequestSendFailedEvent) {
        recyclerAdapter.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendSuggestionAdded(FriendSuggestionAddedEvent friendSuggestionAddedEvent) {
        recyclerAdapter.refresh();
    }

}
