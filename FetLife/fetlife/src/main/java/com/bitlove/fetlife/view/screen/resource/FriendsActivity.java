package com.bitlove.fetlife.view.screen.resource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.db.RelationReference;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Conversation;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.adapter.RelationsRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.view.screen.component.MenuActivityComponent;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;
import com.google.android.material.navigation.NavigationView;

public class FriendsActivity extends ResourceListActivity<Member> implements NavigationView.OnNavigationItemSelectedListener {

    public static final String EXTRA_FRIEND_LIST_MODE = "com.bitlove.fetlife.extra.friend_list_mode";

    public enum FriendListMode {
        NEW_CONVERSATION,
        FRIEND_PROFILE
    }

    private static final int FRIENDS_PAGE_COUNT = 10;

    private RelationsRecyclerAdapter friendsAdapter;

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
    protected void startResourceCall(int pageCount, int requestedPage) {
        String apiCallAction = getApiCallAction();
        if (apiCallAction == null) {
            return;
        }
        if (requestedPage != DEFAULT_REQUESTED_PAGE) {
            FetLifeApiIntentService.startApiCall(FriendsActivity.this, apiCallAction, Integer.toString(RelationReference.VALUE_RELATIONTYPE_FRIEND), Integer.toString(pageCount), Integer.toString(requestedPage));
        } else {
            FetLifeApiIntentService.startApiCall(FriendsActivity.this, apiCallAction, Integer.toString(RelationReference.VALUE_RELATIONTYPE_FRIEND), Integer.toString(pageCount));
        }
    }

    @Override
    protected ResourceListRecyclerAdapter createRecyclerAdapter(Bundle savedInstanceState) {
        return new RelationsRecyclerAdapter(getFetLifeApplication().getUserSessionManager().getCurrentUser().getId(), RelationReference.VALUE_RELATIONTYPE_FRIEND,getFetLifeApplication());
    }

    @Override
    public void onItemClick(Member friend) {
        switch (getFriendListMode()) {
            case NEW_CONVERSATION:
                //TODO(WEBAPP): implement start new conversation
//                MessagesActivity.startActivity(FriendsActivity.this, Conversation.createLocalConversation(friend), friend.getNickname(), friend.getAvatarLink(), false);
                finish();
                return;
            case FRIEND_PROFILE:
                onAvatarClick(friend);
                return;
        }
    }

    @Override
    public void onAvatarClick(Member friend) {
        ProfileActivity.startActivity(this,friend.getId());
    }
}
