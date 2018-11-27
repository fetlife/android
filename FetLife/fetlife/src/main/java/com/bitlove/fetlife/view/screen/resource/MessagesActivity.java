package com.bitlove.fetlife.view.screen.resource;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.MessageSendFailedEvent;
import com.bitlove.fetlife.event.MessageSendSucceededEvent;
import com.bitlove.fetlife.event.NewConversationEvent;
import com.bitlove.fetlife.event.NewMessageEvent;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.inbound.onesignal.NotificationParser;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Conversation;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Conversation_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Message;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.MessageDuplicationDebugUtil;
import com.bitlove.fetlife.util.SpaceTokenizer;
import com.bitlove.fetlife.view.adapter.MessagesRecyclerAdapter;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.navigation.NavigationView;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.InvalidDBConfiguration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MessagesActivity extends ResourceActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String EXTRA_CONVERSATION_ID = "com.bitlove.fetlife.extra.conversation_id";
    private static final String EXTRA_CONVERSATION_TITLE = "com.bitlove.fetlife.extra.conversation_title";
    private static final String EXTRA_AVATAR_RESOURCE_URL = "com.bitlove.fetlife.extra.avatar_resource_url";
    private static final String EXTRA_SHARE_URL = "com.bitlove.fetlife.extra.share_url";

    private static final int MAX_MEMBER_SUGGESTION = 5;
    private static final int REQUEST_CODE_SHARE_PICTURES = 213;

    private MessagesRecyclerAdapter messagesAdapter;

    private String conversationId;
    private String avatarUrl;
    private String memberId;
    private boolean oldMessageLoadingInProgress;

    protected RecyclerView recyclerView;
    protected LinearLayoutManager recyclerLayoutManager;
    protected View inputLayout;
    protected View inputIcon, shareIcon;
    protected MultiAutoCompleteTextView textInput;
    private Conversation conversation;

    public static void startActivity(Context context, String conversationId, String title, String avatarResourceUrl, boolean newTask) {
        startActivity(context, conversationId, title, avatarResourceUrl, null, newTask);
    }

    public static void startActivity(Context context, String conversationId, String title, String avatarResourceUrl, String shareUrl, boolean newTask) {
        context.startActivity(createIntent(context, conversationId, title, avatarResourceUrl, shareUrl, newTask));
    }

    public static Intent createIntent(Context context, String conversationId, String title, String avatarResourceUrl, boolean newTask) {
        return createIntent(context, conversationId, title, avatarResourceUrl, null, newTask);
    }

    public static Intent createIntent(Context context, String conversationId, String title, String avatarResourceUrl, String shareUrl, boolean newTask) {
        Intent intent = new Intent(context, MessagesActivity.class);
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(EXTRA_CONVERSATION_ID, conversationId);
        intent.putExtra(EXTRA_CONVERSATION_TITLE, title);
        intent.putExtra(EXTRA_AVATAR_RESOURCE_URL, avatarResourceUrl);
        intent.putExtra(EXTRA_SHARE_URL, shareUrl);
        return intent;
    }

    @Override
    protected void onCreateActivityComponents() {
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.coordinator_resource_default);
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

        NotificationParser.Companion.clearNotificationTypeForUrl("messages");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setEnabled(false);

        inputLayout = findViewById(R.id.text_input_layout);
        inputIcon = findViewById(R.id.text_send_icon);
        shareIcon = findViewById(R.id.picture_share_icon);
        textInput = (MultiAutoCompleteTextView) findViewById(R.id.text_input);
        String shareUrl = getIntent().getStringExtra(EXTRA_SHARE_URL);
        if (shareUrl != null) {
            textInput.append(shareUrl+"\n");
        }
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
        shareIcon.setVisibility(View.VISIBLE);

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
        avatarUrl = intent.getStringExtra(EXTRA_AVATAR_RESOURCE_URL);
        memberId = null;

        messagesAdapter = new MessagesRecyclerAdapter(conversationId);
        conversation = messagesAdapter.getConversation();
        if (conversation != null) {
            String draftMessage = conversation.getDraftMessage();
            if (draftMessage != null) {
                textInput.append(draftMessage);
            }
            if (avatarUrl == null) {
                avatarUrl = conversation.getAvatarLink();
            }
            memberId = conversation.getMemberId();
        }

        if (avatarUrl != null) {
            toolBarImage.setVisibility(View.VISIBLE);
            toolBarImage.setImageURI(avatarUrl);
        } else {
            toolBarImage.setVisibility(View.GONE);
        }
        View.OnClickListener toolBarItemClickListener;
        if (memberId != null) {
            toolBarItemClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileActivity.startActivity(MessagesActivity.this,memberId);
                }
            };
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Member member = Member.loadMember(memberId);
                    member.setLastViewedAt(System.currentTimeMillis());
                    member.mergeSave();
                }
            });
        } else {
            toolBarItemClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            };
        }
        toolBarImage.setOnClickListener(toolBarItemClickListener);
        toolBarTitle.setOnClickListener(toolBarItemClickListener);
        setTitle(conversationTitle);

        recyclerView.setAdapter(messagesAdapter);
    }

    public String getConversationId() {
        return conversationId;
    }

    @Override
    protected void onResourceStart() {

        inputLayout = findViewById(R.id.text_input_layout);
        inputIcon = findViewById(R.id.text_send_icon);
        textInput = (MultiAutoCompleteTextView) findViewById(R.id.text_input);
        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String[] parts = s.toString().split(" ");
                List<String> suggesstions = new ArrayList<>();
                for (String part : parts) {
                    if (part.length() < 2 || part.charAt(0) != '@') {
                        continue;
                    }
                    List<Member> possibleMembers = new Select().from(Member.class).where(Member_Table.nickname.like(part.substring(1) + "%")).orderBy(Member_Table.lastViewedAt,false).limit(MAX_MEMBER_SUGGESTION).queryList();
                    for (Member member : possibleMembers) {
                        suggesstions.add("@"+member.getNickname());
                    }
                }
                textInput.setAdapter(new ArrayAdapter<String >(MessagesActivity.this,android.R.layout.simple_dropdown_item_1line,suggesstions.toArray(new String[suggesstions.size()])));
                textInput.setTokenizer(new SpaceTokenizer());
            }
        });

//        textInput.setMovementMethod(LinkMovementMethod.getInstance());
//        textInput.addTextChangedListener(new TextWatcher() {
//            boolean skipNext = false;
//            int beforeLength;
//            abstract class NicknameSpan extends ClickableSpan {
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                beforeLength = s.length();
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() < beforeLength) {
//                    skipNext = true;
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (skipNext) {
//                    skipNext = false;
//                    return;
//                }
//                NicknameSpan[] currentNicknames = s.getSpans(0, s.length(), NicknameSpan.class);
//                for (NicknameSpan nicknameSpan : currentNicknames) {
//                    s.removeSpan(nicknameSpan);
//                }
//                if (TextUtils.isEmpty(s)) {
//                    return;
//                }
//                int cursorPosition = textInput.getSelectionStart();
//                if (cursorPosition < 0) {
//                    return;
//                }
//                String[] parts = s.subSequence(0,cursorPosition).toString().split(" ");
//                String lastPart = parts[parts.length-1];
//                if (lastPart.length() < 3 || lastPart.charAt(0) != '@') {
//                    return;
//                }
//                List<Member> possibleMembers = new Select().from(Member.class).where(Member_Table.nickname.like(lastPart.substring(1) + "%")).queryList();
//                if (possibleMembers.size() != 1) {
//                    return;
//                }
//                skipNext = true;
//                String suggestion = possibleMembers.get(0).getNickname().substring(lastPart.length()-1) + " ";
//                s.insert(cursorPosition,suggestion);
//                s.setSpan(new NicknameSpan() {
//                    @Override
//                    public void onClick(View widget) {
//                        textInput.setText(textInput.getText().toString());
//                    }
//                },cursorPosition,cursorPosition+suggestion.length(),0);
//            }
//        });

//        textInput.setFilters(new InputFilter[]{new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                  //Custom Emoji Support will go here
//        }});

        messagesAdapter.refresh();

        if (!Conversation.isLocal(conversationId)) {

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                {
                    if(!oldMessageLoadingInProgress && dy < 0) {
                        int lastVisibleItem = recyclerLayoutManager.findLastVisibleItemPosition();
                        int totalItemCount = recyclerLayoutManager.getItemCount();

                        if (lastVisibleItem == (totalItemCount-1)) {
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

        //Workaround for Android OS crash issue
        if (recyclerView != null) {
            recyclerView.stopScroll();
        }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SHARE_PICTURES && resultCode == RESULT_OK) {
            String[] selectedUrls = data.getStringArrayExtra(PictureShareActivity.RESULT_STRINGS_URLS);
            if (textInput.getText() != null && textInput.getText().length() > 0) {
                textInput.append("\n");
            }
            for (String selectedUrl : selectedUrls) {
                textInput.getText().append(selectedUrl + "\n");
            }
        }
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
                FetLifeApiIntentService.startApiCall(MessagesActivity.this.getApplicationContext(),FetLifeApiIntentService.ACTION_APICALL_NOTIFICATION_COUNTS);
            }
        }).run();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessagesCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_MESSAGES) {
            //TODO: solve setting this value false only if appropriate message call is finished (otherwise same call can be triggered twice)
            oldMessageLoadingInProgress = false;
            messagesAdapter.refresh();
            setMessagesRead();
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
            if (messageSendFailedEvent.isForbidden()) {
                showToast(getString(R.string.message_member_forbidden));
            }
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

    private long lastSendButtonClickTime = 0l;
    private static final long SEND_BUTTON_CLICK_THRESHOLD = 700l;

    public void onShare(View v) {
        PictureShareActivity.startActivityForResult(this,REQUEST_CODE_SHARE_PICTURES);
    }

    public void onSend(View v) {
        final String text = textInput.getText().toString();

        if (text == null || text.trim().length() == 0) {
            return;
        }

        Member currentUser = getFetLifeApplication().getUserSessionManager().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        long messageDate = System.currentTimeMillis();
        if (MessageDuplicationDebugUtil.checkTypedMessage(currentUser.getId(),text) && messageDate - lastSendButtonClickTime < SEND_BUTTON_CLICK_THRESHOLD) {
            return;
        }
        lastSendButtonClickTime = System.currentTimeMillis();

        textInput.setText("");

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
        if (conversation != null) {
            conversation.setDraftMessage("");
            conversation.save();

        }

        messagesAdapter.refresh();
    }
}
