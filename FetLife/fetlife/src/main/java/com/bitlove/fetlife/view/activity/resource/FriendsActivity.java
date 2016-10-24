package com.bitlove.fetlife.view.activity.resource;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.Conversation;
import com.bitlove.fetlife.model.pojos.Friend;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.activity.component.MenuActivityComponent;
import com.bitlove.fetlife.view.adapter.FriendsRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FriendsActivity extends ResourceListActivity<Friend> implements NavigationView.OnNavigationItemSelectedListener {

    public static final String EXTRA_FRIEND_LIST_MODE = "com.bitlove.fetlife.extra.friend_list_mode";

    public enum FriendListMode {
        NEW_CONVERSATION,
        FRIEND_PROFILE
    }

    private static final int FRIENDS_PAGE_COUNT = 10;

    private FriendsRecyclerAdapter friendsAdapter;

    private int requestedPage = 1;

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context, FriendListMode.FRIEND_PROFILE));
    }

    public static void startActivity(Context context, FriendListMode friendListMode) {
        context.startActivity(createIntent(context, friendListMode));
    }

    public static Intent createIntent(Context context, FriendListMode friendListMode) {
        Intent intent = new Intent(context, FriendsActivity.class);
        intent.putExtra(EXTRA_FRIEND_LIST_MODE, friendListMode.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onCreateActivityComponents() {
        addActivityComponent(new MenuActivityComponent());
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {
        super.onResourceCreate(savedInstanceState);

        switch (getFriendListMode()) {
            case FRIEND_PROFILE:
                setTitle(R.string.title_activity_friends);
                break;
            case NEW_CONVERSATION:
                setTitle(R.string.title_activity_friends_new_conversation);
                break;
        }
    }

    private FriendListMode getFriendListMode() {
        return FriendListMode.valueOf(getIntent().getStringExtra(EXTRA_FRIEND_LIST_MODE));
    }

    @Override
    protected String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_FRIENDS;
    }

    @Override
    protected ResourceListRecyclerAdapter createRecyclerAdapter(Bundle savedInstanceState) {
        return new FriendsRecyclerAdapter(getFetLifeApplication().getImageLoader());
    }

    @Override
    public void onItemClick(Friend friend) {
        switch (getFriendListMode()) {
            case NEW_CONVERSATION:
                MessagesActivity.startActivity(FriendsActivity.this, Conversation.createLocalConversation(friend), friend.getNickname(), false);
                finish();
                return;
            case FRIEND_PROFILE:
                onAvatarClick(friend);
                return;
        }
    }

    @Override
    public void onAvatarClick(Friend friend) {
        String url = friend.getLink();
        if (url != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }
}
