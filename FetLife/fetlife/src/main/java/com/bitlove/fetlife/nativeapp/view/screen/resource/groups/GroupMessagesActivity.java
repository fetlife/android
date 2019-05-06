package com.bitlove.fetlife.nativeapp.view.screen.resource.groups;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.event.GroupMessageSendFailedEvent;
import com.bitlove.fetlife.nativeapp.event.GroupMessageSendSucceededEvent;
import com.bitlove.fetlife.nativeapp.event.NewGroupMessageEvent;
import com.bitlove.fetlife.nativeapp.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.nativeapp.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.nativeapp.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.nativeapp.inbound.onesignal.NotificationParser;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.GroupComment;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member_Table;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.util.MessageDuplicationDebugUtil;
import com.bitlove.fetlife.nativeapp.util.SpaceTokenizer;
import com.bitlove.fetlife.nativeapp.view.adapter.GroupMessagesRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.screen.resource.PictureShareActivity;
import com.bitlove.fetlife.nativeapp.view.screen.resource.ResourceActivity;
import com.bitlove.fetlife.nativeapp.view.screen.resource.profile.ProfileActivity;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.navigation.NavigationView;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.InvalidDBConfiguration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//import com.bitlove.fetlife.nativeapp.event.NewGroupPostEvent;

public class GroupMessagesActivity extends ResourceActivity
        implements NavigationView.OnNavigationItemSelectedListener, GroupMessagesRecyclerAdapter.GroupMessageClickListener {

    private static final String EXTRA_GROUP_ID = "com.bitlove.fetlife.extra.group_id";
    private static final String EXTRA_GROUP_DISUCSSION_ID = "com.bitlove.fetlife.extra.groupDiscussion_id";
    private static final String EXTRA_DISCUSSION_TITLE = "com.bitlove.fetlife.extra.groupDiscussion_title";
    private static final String EXTRA_AVATAR_RESOURCE_URL = "com.bitlove.fetlife.extra.avatar_resource_url";
    private static final int MAX_MEMBER_SUGGESTION = 5;
    private static final int REQUEST_CODE_SHARE_PICTURES = 213;

    private GroupMessagesRecyclerAdapter messagesAdapter;

    private String avatarUrl;
    private String memberId;

    protected RecyclerView recyclerView;
    protected LinearLayoutManager recyclerLayoutManager;
    protected View inputLayout, inputIcon, floatingArrow, shareIcon;
    protected MultiAutoCompleteTextView textInput;

    private String groupId;
    private String groupDiscussionId;
    private Group group;
    private GroupPost groupDiscussion;

    public static void startActivity(Context context, String groupId, String groupDiscussionId, String title, String avatarResourceUrl, boolean newTask) {
        context.startActivity(createIntent(context, groupId, groupDiscussionId, title, avatarResourceUrl, newTask));
    }

    public static Intent createIntent(Context context, String groupId, String groupDiscussionId, String title, String avatarResourceUrl, boolean newTask) {
        Intent intent = new Intent(context, GroupMessagesActivity.class);
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.putExtra(EXTRA_GROUP_DISUCSSION_ID, groupDiscussionId);
        intent.putExtra(EXTRA_DISCUSSION_TITLE, title);
        intent.putExtra(EXTRA_AVATAR_RESOURCE_URL, avatarResourceUrl);
        return intent;
    }

    public String getGroupDiscussionId() {
        return groupDiscussionId;
    }

    public String getGroupId() {
        return groupId;
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

        //Not ideal but clear all
        NotificationParser.Companion.clearNotificationTypeForUrl("group_messages");
        NotificationParser.Companion.clearNotificationTypeForUrl("group_discussions");

        findViewById(R.id.text_preview).setVisibility(View.GONE);

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
                    if (part.length() < 3 || part.charAt(0) != '@') {
                        continue;
                    }
                    List<Member> possibleMembers = new Select().from(Member.class).where(Member_Table.nickname.like(part.substring(1) + "%")).orderBy(Member_Table.lastViewedAt,false).limit(MAX_MEMBER_SUGGESTION).queryList();
                    for (Member member : possibleMembers) {
                        suggesstions.add("@"+member.getNickname());
                    }
                }
                textInput.setAdapter(new ArrayAdapter<String >(GroupMessagesActivity.this,android.R.layout.simple_dropdown_item_1line,suggesstions.toArray(new String[suggesstions.size()])));
                textInput.setTokenizer(new SpaceTokenizer());
            }
        });
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

        floatingArrow = findViewById(R.id.floating_arrow);
        floatingArrow.setVisibility(View.VISIBLE);
        floatingArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(messagesAdapter.getItemCount()-1);
            }
        });

        setGroupPost(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        setGroupPost(intent);
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

    @Override
    public void onMemberClick(String memberId) {
        ProfileActivity.startActivity(this,memberId);
    }

    @Override
    public void onMessageMetaClicked(String meta) {
        if (!textInput.getText().toString().endsWith(meta)) {
            textInput.append(meta);
        }
    }

    @Override
    public void onRequestPageClick(int page) {
        messagesAdapter.refresh();
        startResourceCall(getPageCount(),page);
    }

    private void setGroupPost(Intent intent) {

        group = Group.loadGroup(getIntent().getStringExtra(EXTRA_GROUP_ID));
        groupId = group != null ? group.getId() : getIntent().getStringExtra(EXTRA_GROUP_ID);

        groupDiscussion = GroupPost.loadGroupPost(getIntent().getStringExtra(EXTRA_GROUP_DISUCSSION_ID));
        groupDiscussionId = groupDiscussion != null ? groupDiscussion.getId() : getIntent().getStringExtra(EXTRA_GROUP_DISUCSSION_ID);

        String groupDiscussionTitle = intent.getStringExtra(EXTRA_DISCUSSION_TITLE);
        avatarUrl = intent.getStringExtra(EXTRA_AVATAR_RESOURCE_URL);
        memberId = null;

        if (groupId != null) {
            group = Group.loadGroup(groupId);
            if (group == null) {
                FetLifeApiIntentService.startApiCall(this,FetLifeApiIntentService.ACTION_APICALL_GROUP,groupId);
            }
        } else {
            Crashlytics.logException(new Exception("groupId must not be null"));
        }

        messagesAdapter = new GroupMessagesRecyclerAdapter(groupId,groupDiscussionId,this);
        groupDiscussion = messagesAdapter.getGroupPost();
        if (groupDiscussion != null) {
            String draftMessage = groupDiscussion.getDraftMessage();
            if (draftMessage != null) {
                textInput.append(draftMessage);
            }
            if (avatarUrl == null) {
                avatarUrl = groupDiscussion.getAvatarLink();
            }
            memberId = groupDiscussion.getMemberId();
            setTitle(groupDiscussion.getTitle());
        } else {
            setTitle(groupDiscussionTitle);
        }

        if (avatarUrl != null) {
            toolBarImage.setVisibility(View.VISIBLE);
            toolBarImage.setImageURI(avatarUrl);
        } else {
            toolBarImage.setVisibility(View.GONE);
        }
        View.OnClickListener toolBarImageClickListener;
        if (memberId != null) {
            toolBarImageClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileActivity.startActivity(GroupMessagesActivity.this,memberId);
                }
            };
        } else {
            toolBarImageClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            };
        }
        toolBarImage.setOnClickListener(toolBarImageClickListener);

        View.OnClickListener toolBarTitleClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Group group = Group.loadGroup(groupId);
                GroupActivity.startActivity(GroupMessagesActivity.this, groupId, group != null ? group.getName() : "", false);
            }
        };

        toolBarTitle.setOnClickListener(toolBarTitleClickListener);

        recyclerView.setAdapter(messagesAdapter);

        setInputFields();

        invalidateOptionsMenu();
    }

    private void setInputFields() {
        int visibility = group != null && group.isMemberOfGroup() ? View.VISIBLE : View.GONE;

        inputLayout.setVisibility(visibility);
        inputIcon.setVisibility(visibility);
        shareIcon.setVisibility(visibility);
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
                textInput.setAdapter(new ArrayAdapter<String >(GroupMessagesActivity.this,android.R.layout.simple_dropdown_item_1line,suggesstions.toArray(new String[suggesstions.size()])));
                textInput.setTokenizer(new SpaceTokenizer());
            }
        });
//        textInput.setFilters(new InputFilter[]{new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                  //Custom Emoji Support will go here
//        }});

        messagesAdapter.refresh();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem == messagesAdapter.getItemCount()-1) {
                    floatingArrow.setVisibility(View.GONE);
                } else {
                    floatingArrow.setVisibility(View.VISIBLE);
                }
            }
        });

        showProgress();
        startResourceCall(getPageCount(), 1);
    }

    private int getPageCount() {
        return GroupMessagesRecyclerAdapter.ITEM_PER_PAGE;
    }

    private void startResourceCall(int pageCount, int requestedPage) {
        if (groupId == null || groupDiscussionId == null) {
            Crashlytics.logException(new Exception("groupId and groupDiscussionId must not be null"));
            return;
        }
        FetLifeApiIntentService.startApiCall(GroupMessagesActivity.this, FetLifeApiIntentService.ACTION_APICALL_GROUP, groupId);
        FetLifeApiIntentService.startApiCall(GroupMessagesActivity.this, FetLifeApiIntentService.ACTION_APICALL_GROUP_MESSAGES, groupId, groupDiscussionId, Integer.toString(pageCount), Integer.toString(requestedPage));
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Workaround for Android OS crash issue
        if (recyclerView != null) {
            recyclerView.stopScroll();
        }

        GroupPost groupDiscussion = messagesAdapter.getGroupPost();
        if (groupDiscussion != null) {
            groupDiscussion.setDraftMessage(textInput.getText().toString());
            try {
                groupDiscussion.save();
            } catch (InvalidDBConfiguration idbce) {
                Crashlytics.logException(idbce);
            }
        } else {
            Crashlytics.logException(new Exception("Draft Message could not be saved : GroupPost is bull"));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_follow:
                groupDiscussion.setFollowed(true);
                groupDiscussion.save();
                FetLifeApiIntentService.startApiCall(GroupMessagesActivity.this,FetLifeApiIntentService.ACTION_APICALL_FOLLOW_DISCUSSION,groupId,groupDiscussionId);
                showToast(getString(R.string.message_discussion_followed));
                invalidateOptionsMenu();
                return true;
            case R.id.action_unfollow:
                groupDiscussion.setFollowed(false);
                groupDiscussion.save();
                FetLifeApiIntentService.startApiCall(GroupMessagesActivity.this,FetLifeApiIntentService.ACTION_APICALL_UNFOLLOW_DISCUSSION,groupId,groupDiscussionId);
                showToast(getString(R.string.message_discussion_unfollowed));
                invalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessagesCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_GROUP_MESSAGES) {
            String[] params = serviceCallFinishedEvent.getParams();
            final String groupId = params[0];
            final String groupDiscussionId = params[1];
            if (this.groupId.equals(groupId) && this.groupDiscussionId.equals(groupDiscussionId)) {
                verifyTitle(groupDiscussionId);
                messagesAdapter.refresh();
                dismissProgress();
            }
        } else if (serviceCallFinishedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_GROUP) {
            group = Group.loadGroup(groupId);
            setInputFields();
        }
    }

    private void verifyTitle(String groupDiscussionId) {
        GroupPost groupPost = GroupPost.loadGroupPost(groupDiscussionId);
        if (groupPost != null) {
            setTitle(groupPost.getTitle());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessagesCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_GROUP_MESSAGES) {
            String[] params = serviceCallFailedEvent.getParams();
            final String groupId = params[0];
            final String groupDiscussionId = params[1];
            if (this.groupId.equals(groupId) && this.groupDiscussionId.equals(groupDiscussionId)) {
                //TODO: solve setting this value false only if appropriate message call is failed (otherwise same call can be triggered twice)
                messagesAdapter.refresh();
                dismissProgress();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_GROUP_MESSAGES) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageArrived(NewGroupMessageEvent newMessageEvent) {
        //TODO remove temporary call solution
        startResourceCall(getPageCount(),1);
//        if (!groupDiscussionId.equals(newMessageEvent.getGroupDiscussionId()) || !groupId.equals(newMessageEvent.getGroupId())) {
//            //TODO: display (snackbar?) notification
//        } else {
//            messagesAdapter.refresh();
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageSent(GroupMessageSendSucceededEvent messageSendSucceededEvent) {
        if (groupId.equals(messageSendSucceededEvent.getGroupId()) && groupDiscussionId.equals(messageSendSucceededEvent.getGroupPostId())) {
            messagesAdapter.refresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageSendFailed(GroupMessageSendFailedEvent messageSendFailedEvent) {
        if (groupId.equals(messageSendFailedEvent.getGroupId()) && groupDiscussionId.equals(messageSendFailedEvent.getGroupPostId())) {
            messagesAdapter.refresh();
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onNewGroupPost(NewGroupPostEvent newGroupPostEvent) {
//        if (newGroupPostEvent.getLocalGroupPostId().equals(groupDiscussionId)) {
//            Intent intent = getIntent();
//            intent.putExtra(EXTRA_CONVERSATION_ID, newGroupPostEvent.getGroupPostId());
//            onNewIntent(intent);
//        } else {
//        }
//    }

    private long lastSendButtonClickTime = 0l;
    private static final long SEND_BUTTON_CLICK_THRESHOLD = 700l;

    public void onSend(View v) {
        if (!group.isMemberOfGroup()) {
            showToast(getString(R.string.message_sending_not_members));
            return;
        }

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

        GroupComment message = new GroupComment();
        message.setPending(true);
        message.setDate(System.currentTimeMillis());
        message.setClientId(UUID.randomUUID().toString());
        message.setGroupId(groupId);
        message.setGroupPostId(groupDiscussionId);
        message.setBody(text.trim());
        message.setSenderId(currentUser.getId());
        message.setSenderNickname(currentUser.getNickname());
        message.save();

        FetLifeApiIntentService.startApiCall(GroupMessagesActivity.this, FetLifeApiIntentService.ACTION_APICALL_SEND_GROUP_MESSAGES);

        GroupPost groupDiscussion = messagesAdapter.getGroupPost();
        if (groupDiscussion != null) {
            groupDiscussion.setDraftMessage("");
            groupDiscussion.save();

        }

        messagesAdapter.refresh();
    }

    public void onShare(View v) {
        PictureShareActivity.startActivityForResult(this,REQUEST_CODE_SHARE_PICTURES);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (groupDiscussion != null && groupDiscussion.isFollowed()) {
            getMenuInflater().inflate(R.menu.menu_funollow,menu);
        } else if (groupDiscussion != null) {
            getMenuInflater().inflate(R.menu.menu_follow,menu);
        }
        return true;
    }

}
