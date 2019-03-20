package com.bitlove.fetlife.view.screen.resource;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.NewMessageEvent;
import com.bitlove.fetlife.inbound.onesignal.NotificationParser;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Conversation;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.adapter.ConversationsRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.component.MenuActivityComponent;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.core.app.ActivityOptionsCompat;

public class ConversationsActivity extends ResourceListActivity<Conversation> {

    private static final String EXTRA_SHARE_URL = "EXTRA_SHARE_URL";

    public static void startActivity(Context context, boolean newTask) {
        startActivity(context, null, newTask, null, null);
    }

    public static void startActivity(Context context, String shareUrl, boolean newTask) {
        startActivity(context, shareUrl, newTask, null, null);
    }

    public static void startActivity(Context context, String shareUrl, boolean newTask, View transitionView, String transitionName) {
        if (transitionView != null && context instanceof Activity) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity)context, transitionView, transitionName);
            context.startActivity(createIntent(context, shareUrl, newTask),options.toBundle());
        } else {
            context.startActivity(createIntent(context, shareUrl, newTask));
        }
    }

    public static Intent createIntent(Context context, boolean newTask) {
        return createIntent(context, null, newTask);
    }

    public static Intent createIntent(Context context, String shareUrl, boolean newTask) {
        Intent intent = new Intent(context, ConversationsActivity.class);
        if (!newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        if (shareUrl != null) {
            intent.putExtra(EXTRA_SHARE_URL, shareUrl);
        }
        intent.putExtra(BaseActivity.EXTRA_HAS_BOTTOM_BAR,true);
        intent.putExtra(BaseActivity.EXTRA_SELECTED_BOTTOM_NAV_ITEM,R.id.navigation_bottom_inbox);
        return intent;
    }

    @Override
    protected void onCreateActivityComponents() {
        addActivityComponent(new MenuActivityComponent());
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {
        super.onResourceCreate(savedInstanceState);

        NotificationParser.Companion.clearNotificationTypeForUrl("messages");

//        floatingActionButton.setVisibility(View.VISIBLE);
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FriendsActivity.startActivity(ConversationsActivity.this, FriendsActivity.FriendListMode.NEW_CONVERSATION);
//            }
//        });
//        floatingActionButton.setContentDescription(getString(R.string.button_new_conversation_discription));
    }

    @Override
    protected void onResourceStart() {
        super.onResourceStart();
    }

    @Override
    protected ResourceListRecyclerAdapter<Conversation, ?> createRecyclerAdapter(Bundle savedInstanceState) {
        return new ConversationsRecyclerAdapter();
    }

    @Override
    protected String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_CONVERSATIONS;
    }

    @Override
    public void onItemClick(Conversation conversation) {
        MessagesActivity.startActivity(ConversationsActivity.this, conversation.getId(), conversation.getNickname(), conversation.getAvatarLink(), getIntent().getStringExtra(EXTRA_SHARE_URL), false);
    }

    @Override
    public void onAvatarClick(Conversation conversation) {
        ProfileActivity.startActivity(this,conversation.getMemberId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageArrived(NewMessageEvent newMessageEvent) {
        showProgress();
        if (!FetLifeApiIntentService.isActionInProgress(FetLifeApiIntentService.ACTION_APICALL_CONVERSATIONS)) {
            FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_CONVERSATIONS);
        }
    }

}
