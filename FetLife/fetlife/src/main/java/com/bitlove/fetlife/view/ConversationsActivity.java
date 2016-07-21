package com.bitlove.fetlife.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.NewMessageEvent;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.Conversation;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

public class ConversationsActivity extends ResourceListActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CONVERSATIONS_PAGE_COUNT = 10;

    private FlowContentObserver conversationsModelObserver;
    private ConversationsRecyclerAdapter conversationsAdapter;

    private int requestedPage = 1;

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ConversationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Temporary fix not to force previous version user to log out and login again to get notifications
        //TODO: remove in later releases

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getFetLifeApplication());
        if (getFetLifeApplication().getMe() != null && !preferences.getBoolean("v100FixApplied",false)) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(getFetLifeApplication().CONSTANT_ONESIGNAL_TAG_VERSION,1);
                jsonObject.put(getFetLifeApplication().CONSTANT_ONESIGNAL_TAG_NICKNAME, getFetLifeApplication().getMe().getNickname());
                jsonObject.put(getFetLifeApplication().CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN, getFetLifeApplication().getMe().getNotificationToken());
                OneSignal.sendTags(jsonObject);

                OneSignal.setSubscription(true);

                preferences.edit().putBoolean("v100FixApplied",true);
            } catch (JSONException e) {
                //TODO: error handling
            }
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendsActivity.startActivity(ConversationsActivity.this, FriendsActivity.FriendListMode.NEW_CONVERSATION);
            }
        });
        floatingActionButton.setContentDescription(getString(R.string.button_new_conversation_discription));

        conversationsAdapter = new ConversationsRecyclerAdapter(getFetLifeApplication().getImageLoader());
        conversationsAdapter.setOnItemClickListener(new ConversationsRecyclerAdapter.OnConversationClickListener() {
            @Override
            public void onItemClick(Conversation conversation) {
                MessagesActivity.startActivity(ConversationsActivity.this, conversation.getId(), conversation.getNickname(), false);
            }

            @Override
            public void onAvatarClick(Conversation conversation) {
                String url = conversation.getMemberLink();
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(conversationsAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = recyclerLayoutManager.getChildCount();
                    int pastVisiblesItems = recyclerLayoutManager.findFirstVisibleItemPosition();
                    int lastVisiblePosition = visibleItemCount + pastVisiblesItems;

                    if (lastVisiblePosition >= (requestedPage * CONVERSATIONS_PAGE_COUNT)) {
                        FetLifeApiIntentService.startApiCall(ConversationsActivity.this, FetLifeApiIntentService.ACTION_APICALL_CONVERSATIONS, Integer.toString(CONVERSATIONS_PAGE_COUNT), Integer.toString(++requestedPage));
                    }

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        conversationsModelObserver = new FlowContentObserver();

        if (isFinishing()) {
            return;
        }

        conversationsModelObserver.registerForContentChanges(this, Conversation.class);
        conversationsAdapter.refresh();

        showProgress();
        getFetLifeApplication().getEventBus().register(this);

        if (!FetLifeApiIntentService.isActionInProgress(FetLifeApiIntentService.ACTION_APICALL_CONVERSATIONS)) {
            FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_CONVERSATIONS, Integer.toString(CONVERSATIONS_PAGE_COUNT));
        }

        requestedPage = 1;
    }

    @Override
    protected void onStop() {
        super.onStop();

        conversationsModelObserver.unregisterForContentChanges(this);

        getFetLifeApplication().getEventBus().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConversationsCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_CONVERSATIONS) {
            conversationsAdapter.refresh();
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConversationsCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_CONVERSATIONS) {
            if (serviceCallFailedEvent.isServerConnectionFailed()) {
                showToast(getResources().getString(R.string.error_connection_failed));
            } else {
                showToast(getResources().getString(R.string.error_apicall_failed));
            }
            conversationsAdapter.refresh();
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConversationCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_CONVERSATIONS) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageArrived(NewMessageEvent newMessageEvent) {
        showProgress();
        if (!FetLifeApiIntentService.isActionInProgress(FetLifeApiIntentService.ACTION_APICALL_CONVERSATIONS)) {
            FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_CONVERSATIONS);
        }
    }

}
