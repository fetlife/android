package com.bitlove.fetlife.view.activity.resource;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.MessageSendFailedEvent;
import com.bitlove.fetlife.event.MessageSendSucceededEvent;
import com.bitlove.fetlife.event.NewConversationEvent;
import com.bitlove.fetlife.event.NewMessageEvent;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.Conversation;

import com.bitlove.fetlife.model.pojos.Conversation_Table;
import com.bitlove.fetlife.model.pojos.Message;

import com.bitlove.fetlife.model.pojos.User;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.activity.component.MenuActivityComponent;
import com.bitlove.fetlife.view.adapter.MessagesRecyclerAdapter;
import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.structure.InvalidDBConfiguration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessagesActivity extends ResourceActivity
        implements NavigationView.OnNavigationItemSelectedListener, MenuActivityComponent.MenuActivityCallBack {

    private static final String EXTRA_CONVERSATION_ID = "com.bitlove.fetlife.extra.conversation_id";
    private static final String EXTRA_CONVERSATION_TITLE = "com.bitlove.fetlife.extra.conversation_title";

    private MessagesRecyclerAdapter messagesAdapter;

    private String conversationId;
    private boolean oldMessageLoadingInProgress;

    protected RecyclerView recyclerView;
    protected LinearLayoutManager recyclerLayoutManager;
    protected View inputLayout;
    protected View inputIcon;
    protected EditText textInput;
    private Conversation conversation;

    public static void startActivity(Context context, String conversationId, String title, boolean newTask) {
        context.startActivity(createIntent(context, conversationId, title, newTask));
    }

    public static Intent createIntent(Context context, String conversationId, String title, boolean newTask) {
        Intent intent = new Intent(context, MessagesActivity.class);
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(EXTRA_CONVERSATION_ID, conversationId);
        intent.putExtra(EXTRA_CONVERSATION_TITLE, title);
        return intent;
    }

    @Override
    protected void onCreateActivityComponents() {
        addActivityComponent(new MenuActivityComponent());
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_resource_list);
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setEnabled(false);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.GONE);

        inputLayout = findViewById(R.id.text_input_layout);
        inputIcon = findViewById(R.id.text_send_icon);
        textInput = (EditText) findViewById(R.id.text_input);
//        textInput.setFilters(new InputFilter[]{new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                  //Custom Emoji Support will go here
//        }});

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        inputLayout.setVisibility(View.VISIBLE);
        inputIcon.setVisibility(View.VISIBLE);

        setConversation(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        setConversation(intent);
    }

    private void setConversation(Intent intent) {
        conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID);
        String conversationTitle = intent.getStringExtra(EXTRA_CONVERSATION_TITLE);
        setTitle(conversationTitle);
        messagesAdapter = new MessagesRecyclerAdapter(conversationId);
        conversation = messagesAdapter.getConversation();
        if (conversation != null) {
            String draftMessage = conversation.getDraftMessage();
            if (draftMessage != null) {
                textInput.append(draftMessage);
            }
        }
        recyclerView.setAdapter(messagesAdapter);
    }

    public String getConversationId() {
        return conversationId;
    }

    @Override
    protected void onResourceStart() {

        inputLayout = findViewById(R.id.text_input_layout);
        inputIcon = findViewById(R.id.text_send_icon);
        textInput = (EditText) findViewById(R.id.text_input);
//        textInput.setFilters(new InputFilter[]{new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                  //Custom Emoji Support will go here
//        }});

        messagesAdapter.refresh();

        if (!Conversation.isLocal(conversationId)) {

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (!oldMessageLoadingInProgress && dy < 0) {
                        int lastVisibleItem = recyclerLayoutManager.findLastVisibleItemPosition();
                        int totalItemCount = recyclerLayoutManager.getItemCount();

                        if (lastVisibleItem == (totalItemCount - 1)) {
                            oldMessageLoadingInProgress = true;
                            //TODO: not trigger call if the old messages were already triggered and there was no older message
                            FetLifeApiIntentService.startApiCall(MessagesActivity.this, FetLifeApiIntentService.ACTION_APICALL_MESSAGES, conversationId, Boolean.toString(false));
                        }
                    }
                }
            });

            showProgress();
            FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MESSAGES, conversationId);
        } else if (messagesAdapter.getItemCount() != 0) {
            showProgress();
            FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_SEND_MESSAGES, conversationId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Conversation conversation = messagesAdapter.getConversation();
        if (conversation != null) {
            conversation.setDraftMessage(textInput.getText().toString());
            try {
                conversation.save();
            } catch (InvalidDBConfiguration idbce) {
                Crashlytics.logException(idbce);
            }
        } else {
            Crashlytics.logException(new Exception("Draft Message could not be saved : Conversation is bull"));
        }
    }

    @Override
    public void onBackPressed() {
        if (Conversation.isLocal(conversationId) && messagesAdapter.getItemCount() == 0) {
            //TODO: consider using it in a db thread executor
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new Delete().from(Conversation.class).where(Conversation_Table.id.is(conversationId)).query();
                }
            }).start();
        }
        super.onBackPressed();
    }

    private void setMessagesRead() {
        final List<String> params = new ArrayList<>();
        params.add(conversationId);

        for (int i = 0; i < messagesAdapter.getItemCount(); i++) {
            Message message = messagesAdapter.getItem(i);
            if (!message.getPending() && message.isNewMessage()) {
                params.add(message.getId());
            }
        }

        if (params.size() == 1) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                FetLifeApiIntentService.startApiCall(MessagesActivity.this.getApplicationContext(), FetLifeApiIntentService.ACTION_APICALL_SET_MESSAGES_READ, params.toArray(new String[params.size()]));
            }
        }).run();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessagesCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_MESSAGES) {
            setMessagesRead();
            oldMessageLoadingInProgress = false;
            //TODO: solve setting this value false only if appropriate message call is finished (otherwise same call can be triggered twice)
            messagesAdapter.refresh();
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessagesCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_MESSAGES) {
            //TODO: solve setting this value false only if appropriate message call is failed (otherwise same call can be triggered twice)
            oldMessageLoadingInProgress = false;
            messagesAdapter.refresh();
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_MESSAGES) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageArrived(NewMessageEvent newMessageEvent) {
        if (!conversationId.equals(newMessageEvent.getConversationId())) {
            //TODO: display (snackbar?) notification
        } else {
            messagesAdapter.refresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageSent(MessageSendSucceededEvent messageSendSucceededEvent) {
        if (conversationId.equals(messageSendSucceededEvent.getConversationId())) {
            messagesAdapter.refresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageSendFailed(MessageSendFailedEvent messageSendFailedEvent) {
        if (conversationId.equals(messageSendFailedEvent.getConversationId())) {
            messagesAdapter.refresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewConversation(NewConversationEvent newConversationEvent) {
        if (newConversationEvent.getLocalConversationId().equals(conversationId)) {
            Intent intent = getIntent();
            intent.putExtra(EXTRA_CONVERSATION_ID, newConversationEvent.getConversationId());
            onNewIntent(intent);
        } else {
        }
    }

    public void onSend(View v) {
        String text = textInput.getText().toString();

        if (text == null || text.trim().length() == 0) {
            return;
        }

        text = text + getMessageSignature();
        textInput.setText("");

        User currentUser = getFetLifeApplication().getUserSessionManager().getCurrentUser();

        if (currentUser == null) {
            return;
        }

        Message message = new Message();
        message.setPending(true);
        message.setDate(System.currentTimeMillis());
        message.setClientId(UUID.randomUUID().toString());
        message.setConversationId(conversationId);
        message.setBody(text.trim());
        message.setSenderId(currentUser.getId());
        message.setSenderNickname(currentUser.getNickname());
        message.save();

        FetLifeApiIntentService.startApiCall(MessagesActivity.this, FetLifeApiIntentService.ACTION_APICALL_SEND_MESSAGES);

        Conversation conversation = messagesAdapter.getConversation();
        conversation.setDraftMessage("");
        conversation.save();

        messagesAdapter.refresh();
    }

    @Override
    public boolean finishAtMenuNavigation() {
        return true;
    }


    public String getMessageSignature() {
        // Optional 'Signature' to append to message sent
        SharedPreferences sharedPreferences = getFetLifeApplication().getUserSessionManager().getActiveUserPreferences();
        String signature = sharedPreferences.getString("settings_key_profile_signature", "default_text");

        return "\n\n" + signature;
    }
}