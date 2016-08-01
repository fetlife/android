package com.bitlove.fetlife.model.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.event.AuthenticationFailedEvent;
import com.bitlove.fetlife.event.FriendRequestSendFailedEvent;
import com.bitlove.fetlife.event.FriendRequestSendSucceededEvent;
import com.bitlove.fetlife.event.LoginFailedEvent;
import com.bitlove.fetlife.event.LoginFinishedEvent;
import com.bitlove.fetlife.event.LoginStartedEvent;
import com.bitlove.fetlife.event.MessageSendFailedEvent;
import com.bitlove.fetlife.event.MessageSendSucceededEvent;
import com.bitlove.fetlife.event.NewConversationEvent;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.api.FetLifeApi;
import com.bitlove.fetlife.model.api.FetLifeService;
import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.bitlove.fetlife.model.pojos.AuthBody;
import com.bitlove.fetlife.model.pojos.Conversation;
import com.bitlove.fetlife.model.pojos.Conversation_Table;
import com.bitlove.fetlife.model.pojos.Friend;
import com.bitlove.fetlife.model.pojos.FriendRequest;
import com.bitlove.fetlife.model.pojos.FriendRequest_Table;
import com.bitlove.fetlife.model.pojos.FriendSuggestion;
import com.bitlove.fetlife.model.pojos.FriendSuggestion_Table;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Message;
import com.bitlove.fetlife.model.pojos.Message_Table;
import com.bitlove.fetlife.model.pojos.Token;
import com.bitlove.fetlife.util.EmojiUtil;
import com.bitlove.fetlife.util.NetworkUtil;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import retrofit.Call;
import retrofit.Response;

public class FetLifeApiIntentService extends IntentService {

    public static final String ACTION_APICALL_CONVERSATIONS = "com.bitlove.fetlife.action.apicall.cpnversations";
    public static final String ACTION_APICALL_FRIENDS = "com.bitlove.fetlife.action.apicall.friends";
    public static final String ACTION_APICALL_MESSAGES = "com.bitlove.fetlife.action.apicall.messages";
    public static final String ACTION_APICALL_SEND_MESSAGES = "com.bitlove.fetlife.action.apicall.send_messages";
    public static final String ACTION_APICALL_SET_MESSAGES_READ = "com.bitlove.fetlife.action.apicall.set_messages_read";
    public static final String ACTION_APICALL_LOGON_USER = "com.bitlove.fetlife.action.apicall.logon_user";
    public static final String ACTION_APICALL_FRIENDREQUESTS = "com.bitlove.fetlife.action.apicall.friendrequests";
    public static final String ACTION_APICALL_SEND_FRIENDREQUESTS = "com.bitlove.fetlife.action.apicall.send_friendrequests";

    private static final String CONSTANT_PREF_KEY_REFRESH_TOKEN = "com.bitlove.fetlife.key.pref.token.refresh";
    private static final String EXTRA_PARAMS = "com.bitlove.fetlife.extra.params";
    private static final String PARAM_SORT_ORDER_UPDATED_DESC = "-updated_at";

    private static final int PARAM_NEWMESSAGE_LIMIT = 50;
    private static final int PARAM_OLDMESSAGE_LIMIT = 25;

    private static String actionInProgress = null;

    public FetLifeApiIntentService() {
        super("FetLifeApiIntentService");
    }

    public static synchronized void startApiCall(Context context, String action, String... params) {
        Intent intent = new Intent(context, FetLifeApiIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_PARAMS, params);
        context.startService(intent);
    }

    //TODO: think about being more specific and store also parameters for exact identification
    private static synchronized void setActionInProgress(String action) {
        actionInProgress = action;
    }

    public static synchronized String getActionInProgress() {
        return actionInProgress;
    }

    public static synchronized boolean isActionInProgress(String action) {
        if (actionInProgress == null) {
            return false;
        }
        return actionInProgress.equals(action);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent == null && getFetLifeApplication().getMe() == null) {
            return;
        }

        final String action = intent.getAction();

        if (NetworkUtil.getConnectivityStatus(this) == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            sendConnectionFailedNotification(action);
            return;
        }

        try {

            String[] params = intent.getStringArrayExtra(EXTRA_PARAMS);

            setActionInProgress(action);

            sendLoadStartedNotification(action);

            if (action != ACTION_APICALL_LOGON_USER && getFetLifeApplication().getAccessToken() == null) {
                if (refreshToken()) {
                    onHandleIntent(intent);
                } else {
                    sendAuthenticaionFailedNotification();
                    return;
                }
            }

            boolean result = false;
            switch (action) {
                case ACTION_APICALL_LOGON_USER:
                    result = logonUser(params);
                    break;
                case ACTION_APICALL_CONVERSATIONS:
                    result = retriveConversations(params);
                    break;
                case ACTION_APICALL_FRIENDS:
                    result = retriveFriends(params);
                    break;
                case ACTION_APICALL_FRIENDREQUESTS:
                    result = retriveFriendRequests(params);
                    break;
                case ACTION_APICALL_MESSAGES:
                    result = retrieveMessages(params);
                    break;
                case ACTION_APICALL_SEND_MESSAGES:
                    result = sendPendingMessages(false);
                    break;
                case ACTION_APICALL_SET_MESSAGES_READ:
                    result = setMessagesRead(params);
                    break;
                case ACTION_APICALL_SEND_FRIENDREQUESTS:
                    result = sendPendingFriendRequests();
                    break;
            }

            if (result) {
                sendLoadFinishedNotification(action);
            } else if (action != ACTION_APICALL_LOGON_USER && getFetLifeApplication().getFetLifeService().getLastResponseCode() == 403) {
                if (refreshToken()) {
                    onHandleIntent(intent);
                } else {
                    sendLoadFailedNotification(action);
                }
                //TODO: error handling for endless loop
            } else {
                sendLoadFailedNotification(action);
            }
        } catch (IOException ioe) {
            sendConnectionFailedNotification(action);
        } finally {
            setActionInProgress(null);
        }
    }

    private boolean refreshToken() throws IOException {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getFetLifeApplication());
        String refreshToken = preferences.getString(CONSTANT_PREF_KEY_REFRESH_TOKEN, null);

        if (refreshToken == null) {
            return false;
        }

        Call<Token> tokenRefreshCall = getFetLifeApplication().getFetLifeService().getFetLifeApi().refreshToken(
                BuildConfig.CLIENT_ID,
                BuildConfig.CLIENT_SECRET,
                BuildConfig.REDIRECT_URL,
                FetLifeService.GRANT_TYPE_TOKEN_REFRESH,
                refreshToken
        );

        Response<Token> tokenResponse = tokenRefreshCall.execute();

        if (tokenResponse.isSuccess()) {
            getFetLifeApplication().setAccessToken(tokenResponse.body().getAccessToken());

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(CONSTANT_PREF_KEY_REFRESH_TOKEN, tokenResponse.body().getRefreshToken());
            editor.apply();

            return true;
        } else {
            return false;
        }

    }

    private void sendAuthenticaionFailedNotification() {
        getFetLifeApplication().getEventBus().post(new AuthenticationFailedEvent());
    }

    private void sendLoadStartedNotification(String action) {
        switch (action) {
            case ACTION_APICALL_LOGON_USER:
                getFetLifeApplication().getEventBus().post(new LoginStartedEvent());
                break;
            default:
                getFetLifeApplication().getEventBus().post(new ServiceCallStartedEvent(action));
                break;
        }
    }

    private void sendLoadFinishedNotification(String action) {
        switch (action) {
            case ACTION_APICALL_LOGON_USER:
                getFetLifeApplication().getEventBus().post(new LoginFinishedEvent());
                break;
            default:
                getFetLifeApplication().getEventBus().post(new ServiceCallFinishedEvent(action));
                break;
        }
    }

    private void sendLoadFailedNotification(String action) {
        switch (action) {
            case ACTION_APICALL_LOGON_USER:
                getFetLifeApplication().getEventBus().post(new LoginFailedEvent());
                break;
            default:
                getFetLifeApplication().getEventBus().post(new ServiceCallFailedEvent(action));
                break;
        }
    }

    private void sendConnectionFailedNotification(String action) {
        switch (action) {
            case ACTION_APICALL_LOGON_USER:
                getFetLifeApplication().getEventBus().post(new LoginFailedEvent(true));
                break;
            default:
                getFetLifeApplication().getEventBus().post(new ServiceCallFailedEvent(action, true));
                break;
        }
    }

    private boolean logonUser(String... params) throws IOException {
        Call<Token> tokenCall = getFetLifeApplication().getFetLifeService().getFetLifeApi().login(
                BuildConfig.CLIENT_ID,
                BuildConfig.CLIENT_SECRET,
                BuildConfig.REDIRECT_URL,
                new AuthBody(params[0], params[1]));

        Response<Token> tokenResponse = tokenCall.execute();
        if (tokenResponse.isSuccess()) {
            getFetLifeApplication().setAccessToken(tokenResponse.body().getAccessToken());

            if (retrieveMyself()) {

                Member me = getFetLifeApplication().getMe();

                String meAsJson = me.toJsonString();
                Bundle accountData = new Bundle();
                accountData.putString(FetLifeApplication.CONSTANT_PREF_KEY_ME_JSON, meAsJson);

                //TODO: investigate use of account manager
                //TODO: use keystore for encryption as an alternative
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getFetLifeApplication());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(FetLifeApplication.CONSTANT_PREF_KEY_ME_JSON, meAsJson).putString(CONSTANT_PREF_KEY_REFRESH_TOKEN, tokenResponse.body().getRefreshToken());
                editor.apply();

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(FetLifeApplication.CONSTANT_ONESIGNAL_TAG_VERSION,1);
                    jsonObject.put(FetLifeApplication.CONSTANT_ONESIGNAL_TAG_NICKNAME,me.getNickname());
                    jsonObject.put(FetLifeApplication.CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN,me.getNotificationToken());
                    OneSignal.sendTags(jsonObject);
                } catch (JSONException e) {
                    //TODO: error handling
                }

                OneSignal.setSubscription(true);

                return true;

            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean sendPendingMessages(boolean positiveStackedResult) throws IOException {
        List<Message> pendingMessages = new Select().from(Message.class).where(Message_Table.pending.is(true)).queryList();
        for (Message pendingMessage : pendingMessages) {
            String conversationId = pendingMessage.getConversationId();
            if (Conversation.isLocal(conversationId)) {
                if (startNewConversation(conversationId, pendingMessage)) {
                    //db changed, reload remaining pending messages
                    return sendPendingMessages(true);
                }
            } else {
                if (!sendPendingMessage(pendingMessage)) {
                    pendingMessage.setPending(false);
                    pendingMessage.setFailed(true);
                    pendingMessage.save();
                } else if (!positiveStackedResult) {
                    positiveStackedResult = true;
                }
            }
        }
        return positiveStackedResult;
    }

    private boolean sendPendingMessage(Message pendingMessage) throws IOException {
        Call<Message> postMessagesCall = getFetLifeApi().postMessage(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken(), pendingMessage.getConversationId(), EmojiUtil.escapeEmojis(pendingMessage.getBody()));
        Response<Message> postMessageResponse = postMessagesCall.execute();
        String conversationId = pendingMessage.getConversationId();
        if (postMessageResponse.isSuccess()) {
            final Message message = postMessageResponse.body();
            message.setClientId(pendingMessage.getClientId());
            message.setPending(false);
            message.setConversationId(conversationId);
            message.setBody(EmojiUtil.deEscapeEmojis(message.getBody()));
            message.update();
            getFetLifeApplication().getEventBus().post(new MessageSendSucceededEvent(conversationId));
            return true;
        } else {
            getFetLifeApplication().getEventBus().post(new MessageSendFailedEvent(conversationId));
            return false;
        }
    }

    private boolean sendPendingFriendRequests() throws IOException {

        boolean stackedResult = false;
        List<FriendRequest> pendingFriendRequests = new Select().from(FriendRequest.class).where(FriendRequest_Table.pending.is(true)).queryList();
        for (FriendRequest pendingFriendRequest : pendingFriendRequests) {
            if (!sendPendingFriendRequest(pendingFriendRequest)) {
                pendingFriendRequest.delete();
            } else if (!stackedResult) {
                stackedResult = true;
            }
        }
        List<FriendSuggestion> pendingFriendSuggestions = new Select().from(FriendSuggestion.class).where(FriendSuggestion_Table.pending.is(true)).queryList();
        for (FriendSuggestion pendingFriendSuggestion : pendingFriendSuggestions) {
            if (!sendPendingFriendSuggestion(pendingFriendSuggestion)) {
                pendingFriendSuggestion.delete();
            } else if (!stackedResult) {
                stackedResult = true;
            }
        }
        return stackedResult;
    }

    private boolean sendPendingFriendSuggestion(FriendSuggestion pendingFriendSuggestion) throws IOException {
        Call<FriendRequest> createFriendRequestCall = getFetLifeApi().createFriendRequest(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken(), pendingFriendSuggestion.getId());
        Response<FriendRequest> friendRequestResponse = createFriendRequestCall.execute();
        if (friendRequestResponse.isSuccess()) {
            pendingFriendSuggestion.delete();
            getFetLifeApplication().getEventBus().post(new FriendRequestSendSucceededEvent());
            return true;
        } else {
            getFetLifeApplication().getEventBus().post(new FriendRequestSendFailedEvent());
            return false;
        }
    }

    private boolean sendPendingFriendRequest(FriendRequest pendingFriendRequest) throws IOException {
        Call<FriendRequest> friendRequestsCall;
        switch (pendingFriendRequest.getPendingState()) {
            case ACCEPTED:
                friendRequestsCall = getFetLifeApi().acceptFriendRequests(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken(), pendingFriendRequest.getId());
                break;
            case REJECTED:
                friendRequestsCall = getFetLifeApi().removeFriendRequests(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken(), pendingFriendRequest.getId());
                break;
            default:
                return false;
        }

        Response<FriendRequest> friendRequestResponse = friendRequestsCall.execute();
        if (friendRequestResponse.isSuccess()) {
            pendingFriendRequest.delete();
            getFetLifeApplication().getEventBus().post(new FriendRequestSendSucceededEvent());
            return true;
        } else {
            pendingFriendRequest.delete();
            getFetLifeApplication().getEventBus().post(new FriendRequestSendFailedEvent());
            return false;
        }
    }

    private boolean startNewConversation(String localConversationId, Message startMessage) throws IOException {

        Conversation pendingConversation = new Select().from(Conversation.class).where(Conversation_Table.id.is(localConversationId)).querySingle();
        if (pendingConversation == null) {
            return false;
        }

        Call<Conversation> postConversationCall = getFetLifeApi().postConversation(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken(), pendingConversation.getMemberId(), startMessage.getBody(), startMessage.getBody());
        Response<Conversation> postConversationResponse = postConversationCall.execute();
        if (postConversationResponse.isSuccess()) {
            pendingConversation.delete();

            Conversation conversation = postConversationResponse.body();
            conversation.save();

            String serverConversationId = conversation.getId();
            startMessage.delete();
            retrieveMessages(serverConversationId);

            List<Message> pendingMessages = new Select().from(Message.class).where(Message_Table.conversationId.is(localConversationId)).queryList();
            for (Message pendingMessage : pendingMessages) {
                pendingMessage.setConversationId(serverConversationId);
                pendingMessage.save();
            }

            getFetLifeApplication().getEventBus().post(new NewConversationEvent(localConversationId, serverConversationId));
            return true;
        } else {
            return false;
        }
    }

    private boolean retrieveMessages(String... params) throws IOException {
        final String conversationId = params[0];

        final boolean loadNewMessages = getBoolFromParams(params, 1, true);

        Call<List<Message>> getMessagesCall = null;
        if (loadNewMessages) {
            String selfId = getFetLifeApplication().getMe().getId();
            Message newestMessage = new Select().from(Message.class).where(Message_Table.conversationId.is(conversationId)).and(Message_Table.senderId.isNot(selfId)).orderBy(Message_Table.date, false).querySingle();
            getMessagesCall = getFetLifeApi().getMessages(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken(), conversationId, newestMessage != null ? newestMessage.getId() : null, null, PARAM_NEWMESSAGE_LIMIT);
        } else {
            Message oldestMessage = new Select().from(Message.class).where(Message_Table.conversationId.is(conversationId)).and(Message_Table.pending.is(false)).orderBy(Message_Table.date,true).querySingle();
            getMessagesCall = getFetLifeApi().getMessages(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken(), conversationId, null, oldestMessage != null ? oldestMessage.getId() : null, PARAM_OLDMESSAGE_LIMIT);
        }

        //TODO solve edge case when there is the gap between last message in db and the retrieved messages (e.g. when because of the limit not all recent messages could be retrieved)

        Response<List<Message>> messagesResponse = getMessagesCall.execute();
        if (messagesResponse.isSuccess()) {
            final List<Message> messages = messagesResponse.body();
            FlowManager.getDatabase(FetLifeDatabase.class).executeTransaction(new ITransaction() {
                @Override
                public void execute(DatabaseWrapper databaseWrapper) {
                    for (Message message : messages) {
                        Message storedMessage = new Select().from(Message.class).where(Message_Table.id.is(message.getId())).querySingle();
                        if (storedMessage != null) {
                            message.setClientId(storedMessage.getClientId());
                        } else {
                            message.setClientId(UUID.randomUUID().toString());
                        }
                        message.setBody(EmojiUtil.deEscapeEmojis(message.getBody()));
                        message.setConversationId(conversationId);
                        message.setPending(false);
                        message.save();
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private boolean setMessagesRead(String[] params) throws IOException {
        String conversationId = params[0];
        String[] messageIds = Arrays.copyOfRange(params, 1, params.length);
        Call<ResponseBody> setMessagesReadCall = getFetLifeApi().setMessagesRead(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken(), conversationId, messageIds);
        Response<ResponseBody> response = setMessagesReadCall.execute();
        return response.isSuccess();
    }

    private boolean retriveConversations(String[] params) throws IOException {
        final int limit = getIntFromParams(params, 0, 10);
        final int page = getIntFromParams(params, 1, 1);

        Call<List<Conversation>> getConversationsCall = getFetLifeApi().getConversations(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken(), PARAM_SORT_ORDER_UPDATED_DESC, limit, page);
        Response<List<Conversation>> conversationsResponse = getConversationsCall.execute();
        if (conversationsResponse.isSuccess()) {
            final List<Conversation> conversations = conversationsResponse.body();
            FlowManager.getDatabase(FetLifeDatabase.class).executeTransaction(new ITransaction() {
                @Override
                public void execute(DatabaseWrapper databaseWrapper) {
                    for (Conversation conversation : conversations) {
                        conversation.save();
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private boolean retriveFriends(String[] params) throws IOException {
        final int limit = getIntFromParams(params, 0, 10);
        final int page = getIntFromParams(params, 1, 1);

        Call<List<Friend>> getFriendsCall = getFetLifeApi().getFriends(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken(), limit, page);
        Response<List<Friend>> friendsResponse = getFriendsCall.execute();
        if (friendsResponse.isSuccess()) {
            final List<Friend> friends = friendsResponse.body();
            FlowManager.getDatabase(FetLifeDatabase.class).executeTransaction(new ITransaction() {
                @Override
                public void execute(DatabaseWrapper databaseWrapper) {
                    for (Friend friend : friends) {
                        friend.save();
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private boolean retriveFriendRequests(String[] params) throws IOException {
        final int limit = getIntFromParams(params, 0, 10);
        final int page = getIntFromParams(params, 1, 1);

        Call<List<FriendRequest>> getFriendRequestsCall = getFetLifeApi().getFriendRequests(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken(), limit, page);
        Response<List<FriendRequest>> friendRequestsResponse = getFriendRequestsCall.execute();
        if (friendRequestsResponse.isSuccess()) {
            final List<FriendRequest> friendRequests = friendRequestsResponse.body();
            FlowManager.getDatabase(FetLifeDatabase.class).executeTransaction(new ITransaction() {
                @Override
                public void execute(DatabaseWrapper databaseWrapper) {
                    for (FriendRequest friendRequest : friendRequests) {
                        FriendRequest storedFriendRequest = new Select().from(FriendRequest.class).where(FriendRequest_Table.id.is(friendRequest.getId())).querySingle();
                        if (storedFriendRequest != null) {
                            //skip
                        } else {
                            friendRequest.setClientId(UUID.randomUUID().toString());
                            friendRequest.save();
                        }
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private int getIntFromParams(String[] params, int pageParamPosition, int defaultValue) {
        int param = defaultValue;
        if (params != null && params.length > pageParamPosition) {
            try {
                param = Integer.parseInt(params[pageParamPosition]);
            } catch (NumberFormatException nfe) {
            }
        }
        return param;
    }

    private boolean getBoolFromParams(String[] params, int pageParamPosition, boolean defaultValue) {
        boolean param = defaultValue;
        if (params != null && params.length > pageParamPosition) {
            try {
                param = Boolean.parseBoolean(params[pageParamPosition]);
            } catch (NumberFormatException nfe) {
            }
        }
        return param;
    }

    private boolean retrieveMyself() throws IOException {
        Call<Member> getMeCall = getFetLifeApi().getMe(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken());
        Response<Member> getMeResponse = getMeCall.execute();
        if (getMeResponse.isSuccess()) {
            getFetLifeApplication().setMe(getMeResponse.body());
            return true;
        } else {
            return false;
        }
    }

    private FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplication();
    }

    private FetLifeApi getFetLifeApi() {
        return getFetLifeApplication().getFetLifeService().getFetLifeApi();
    }
}
