package com.bitlove.fetlife.model.service;

import android.app.IntentService;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.event.AuthenticationFailedEvent;
import com.bitlove.fetlife.event.EventsByLocationRetrieveFailedEvent;
import com.bitlove.fetlife.event.EventsByLocationRetrievedEvent;
import com.bitlove.fetlife.event.FriendRequestResponseSendFailedEvent;
import com.bitlove.fetlife.event.FriendRequestResponseSendSucceededEvent;
import com.bitlove.fetlife.event.GroupMessageSendFailedEvent;
import com.bitlove.fetlife.event.GroupMessageSendSucceededEvent;
import com.bitlove.fetlife.event.LatestReleaseEvent;
import com.bitlove.fetlife.event.LoginFailedEvent;
import com.bitlove.fetlife.event.LoginFinishedEvent;
import com.bitlove.fetlife.event.LoginStartedEvent;
import com.bitlove.fetlife.event.MessageSendFailedEvent;
import com.bitlove.fetlife.event.MessageSendSucceededEvent;
import com.bitlove.fetlife.event.NewConversationEvent;
import com.bitlove.fetlife.event.NotificationCountUpdatedEvent;
import com.bitlove.fetlife.event.PictureUploadFailedEvent;
import com.bitlove.fetlife.event.PictureUploadFinishedEvent;
import com.bitlove.fetlife.event.PictureUploadStartedEvent;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.event.VideoChunkUploadCancelEvent;
import com.bitlove.fetlife.event.VideoChunkUploadFailedEvent;
import com.bitlove.fetlife.event.VideoChunkUploadFinishedEvent;
import com.bitlove.fetlife.event.VideoChunkUploadStartedEvent;
import com.bitlove.fetlife.event.VideoUploadFailedEvent;
import com.bitlove.fetlife.model.api.FetLifeApi;
import com.bitlove.fetlife.model.api.FetLifeMultipartUploadApi;
import com.bitlove.fetlife.model.api.FetLifeService;
import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.bitlove.fetlife.model.pojos.fetlife.db.EventReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.EventReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.db.EventRsvpReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.EventRsvpReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.db.FollowRequest;
import com.bitlove.fetlife.model.pojos.fetlife.db.GroupDiscussionReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.GroupDiscussionReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.db.GroupMembershipReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.GroupMembershipReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.db.PictureReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.PictureReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.db.RelationReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.RelationReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.db.SharedProfile;
import com.bitlove.fetlife.model.pojos.fetlife.db.SharedProfile_Table;
import com.bitlove.fetlife.model.pojos.fetlife.db.StatusReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.StatusReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.db.VideoReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.VideoReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.db.WritingReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.WritingReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Conversation;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Conversation_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.FriendRequest;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.FriendRequest_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.GroupComment;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.GroupComment_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.GroupPost_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Message;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Message_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Relationship;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Relationship_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Status;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Video;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Writing;
import com.bitlove.fetlife.model.pojos.fetlife.json.AppId;
import com.bitlove.fetlife.model.pojos.fetlife.json.AuthBody;
import com.bitlove.fetlife.model.pojos.fetlife.json.Feed;
import com.bitlove.fetlife.model.pojos.fetlife.json.GroupMembership;
import com.bitlove.fetlife.model.pojos.fetlife.json.NotificationCounts;
import com.bitlove.fetlife.model.pojos.fetlife.json.Rsvp;
import com.bitlove.fetlife.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.model.pojos.fetlife.json.Token;
import com.bitlove.fetlife.model.pojos.fetlife.json.VideoUploadResult;
import com.bitlove.fetlife.model.pojos.github.Release;
import com.bitlove.fetlife.session.UserSessionManager;
import com.bitlove.fetlife.util.BytesUtil;
import com.bitlove.fetlife.util.DateUtil;
import com.bitlove.fetlife.util.FileUtil;
import com.bitlove.fetlife.util.LogUtil;
import com.bitlove.fetlife.util.MapUtil;
import com.bitlove.fetlife.util.MessageDuplicationDebugUtil;
import com.bitlove.fetlife.util.NetworkUtil;
import com.bitlove.fetlife.util.ServerIdUtil;
import com.bitlove.fetlife.util.VersionUtil;
import com.bitlove.fetlife.view.adapter.feed.FeedItemResourceHelper;
import com.bitlove.fetlife.view.screen.resource.ExploreActivity;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.InvalidDBConfiguration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


//TODO use thread executor instead of new threads
public class FetLifeApiIntentService extends JobIntentService {

    private static final String FEATURE_QUESTIONS = "questions";
    private static final int JOB_ID = 42;

    public FetLifeApiIntentService() {
        super();
    }

    //****
    //Action names for Api service calls
    //***

    public static final String ACTION_APICALL_MEMBER = "com.bitlove.fetlife.action.apicall.member";
    public static final String ACTION_APICALL_SEARCH_MEMBER = "com.bitlove.fetlife.action.apicall.search_member";
    public static final String ACTION_APICALL_MEMBER_RELATIONS = "com.bitlove.fetlife.action.apicall.member_relations";
    public static final String ACTION_APICALL_MEMBER_STATUSES = "com.bitlove.fetlife.action.apicall.member_statuses";
    public static final String ACTION_APICALL_MEMBER_EVENTS = "com.bitlove.fetlife.action.apicall.member_events";
    public static final String ACTION_APICALL_MEMBER_WRITINGS = "com.bitlove.fetlife.action.apicall.member_writings";
    public static final String ACTION_APICALL_MEMBER_GROUPS = "com.bitlove.fetlife.action.apicall.member_groups";
    public static final String ACTION_APICALL_MEMBER_PICTURES = "com.bitlove.fetlife.action.apicall.member_pictures";
    public static final String ACTION_APICALL_MEMBER_VIDEOS = "com.bitlove.fetlife.action.apicall.member_videos";
    public static final String ACTION_APICALL_MEMBER_PICTURE = "com.bitlove.fetlife.action.apicall.member_picture";
    public static final String ACTION_APICALL_MEMBER_VIDEO = "com.bitlove.fetlife.action.apicall.member_video";
    public static final String ACTION_APICALL_CONVERSATIONS = "com.bitlove.fetlife.action.apicall.conversations";
    public static final String ACTION_APICALL_FEED = "com.bitlove.fetlife.action.apicall.feed";
    public static final String ACTION_APICALL_EXPLORE = "com.bitlove.fetlife.action.apicall.explore";
    public static final String ACTION_APICALL_MEMBER_FEED = "com.bitlove.fetlife.action.apicall.member_feed";
    public static final String ACTION_APICALL_FRIENDS = "com.bitlove.fetlife.action.apicall.friends";
    public static final String ACTION_APICALL_MESSAGES = "com.bitlove.fetlife.action.apicall.messages";
    public static final String ACTION_APICALL_GROUP_MESSAGES = "com.bitlove.fetlife.action.apicall.group_messages";
    public static final String ACTION_APICALL_SEND_MESSAGES = "com.bitlove.fetlife.action.apicall.send_messages";
    public static final String ACTION_APICALL_SEND_GROUP_MESSAGES = "com.bitlove.fetlife.action.apicall.send_group_messages";
    public static final String ACTION_APICALL_SET_MESSAGES_READ = "com.bitlove.fetlife.action.apicall.set_messages_read";
    public static final String ACTION_APICALL_ADD_LOVE = "com.bitlove.fetlife.action.apicall.add_love";
    public static final String ACTION_APICALL_REMOVE_LOVE = "com.bitlove.fetlife.action.apicall.remove_love";
    public static final String ACTION_APICALL_LOGON_USER = "com.bitlove.fetlife.action.apicall.logon_user";
    public static final String ACTION_APICALL_FRIENDREQUESTS = "com.bitlove.fetlife.action.apicall.friendrequests";
    public static final String ACTION_APICALL_CANCEL_FRIENDREQUEST = "com.bitlove.fetlife.action.apicall.cancel_friendrequest";
    public static final String ACTION_APICALL_CANCEL_FRIENDSHIP = "com.bitlove.fetlife.action.apicall.cancel_friendship";
    public static final String ACTION_APICALL_PENDING_RELATIONS = "com.bitlove.fetlife.action.apicall.pendingrelations";
    public static final String ACTION_APICALL_UPLOAD_PICTURE = "com.bitlove.fetlife.action.apicall.upload_picture";
    public static final String ACTION_APICALL_UPLOAD_VIDEO = "com.bitlove.fetlife.action.apicall.upload_video";
    public static final String ACTION_APICALL_UPLOAD_VIDEO_CHUNK = "com.bitlove.fetlife.action.apicall.upload_video_chunk";
    public static final String ACTION_APICALL_SEARCH_EVENT_BY_LOCATION = "com.bitlove.fetlife.action.apicall.search_events_by_location";
    public static final String ACTION_APICALL_SEARCH_EVENT_BY_TAG = "com.bitlove.fetlife.action.apicall.search_events_by_tag";
    public static final String ACTION_APICALL_EVENT = "com.bitlove.fetlife.action.apicall.event";
    public static final String ACTION_APICALL_WRITING = "com.bitlove.fetlife.action.apicall.writing";
    public static final String ACTION_APICALL_SEARCH_GROUP = "com.bitlove.fetlife.action.apicall.search_group";
    public static final String ACTION_APICALL_GROUP = "com.bitlove.fetlife.action.apicall.group";
    public static final String ACTION_APICALL_GROUP_JOIN = "com.bitlove.fetlife.action.apicall.group_join";
    public static final String ACTION_APICALL_FOLLOW_DISCUSSION = "com.bitlove.fetlife.action.apicall.follow_discussion";
    public static final String ACTION_APICALL_UNFOLLOW_DISCUSSION = "com.bitlove.fetlife.action.apicall.unfollow_discussion";
    public static final String ACTION_APICALL_NOTIFICATION_COUNTS = "com.bitlove.fetlife.action.apicall.notification_counts";

    public static final String ACTION_APICALL_GROUP_LEAVE = "com.bitlove.fetlife.action.apicall.group_leave";
    public static final String ACTION_APICALL_EVENT_RSVPS = "com.bitlove.fetlife.action.apicall.event.ravps";
    public static final String ACTION_APICALL_SET_RSVP_STATUS = "com.bitlove.fetlife.action.apicall.event.set_ravp";
    public static final String ACTION_APICALL_GROUP_MEMBERS = "com.bitlove.fetlife.action.apicall.group_members";
    public static final String ACTION_APICALL_GROUP_DISCUSSIONS = "com.bitlove.fetlife.action.apicall.group_discussions";

    public static final String ACTION_APICALL_CHECK_4_QUESTIONS = "com.bitlove.fetlife.action.apicall.check_4_questions";

    public static final String ACTION_CANCEL_UPLOAD_VIDEO_CHUNK = "com.bitlove.fetlife.action.cancel.upload_video_chunk";

    public static final String ACTION_EXTERNAL_CALL_CHECK_4_UPDATES = "com.bitlove.fetlife.action.external.check_for_updates";

    private static final String PARAM_VALUE_FRIEND_REQUEST_SENT = "sent";
    private static final String PARAM_VALUE_FRIEND_REQUEST_RECEIVED = "received";

    private static final String PARAM_VALUE_EVENT_RSVP_YES = "yes";
    private static final String PARAM_VALUE_EVENT_RSVP_MAYBE = "maybe";

    //Incoming intent extra parameter name
    private static final String EXTRA_PARAMS = "com.bitlove.fetlife.extra.params";

    //Backend variable for sorting based on updated field
    private static final String PARAM_SORT_ORDER_UPDATED_DESC = "-updated_at";

    //Default limits for retrieving the most recent messages from the backend
    private static final int PARAM_NEWMESSAGE_LIMIT = 50;

    //Default limits for retrieving previous messages from the message history
    private static final int PARAM_OLDMESSAGE_LIMIT = 25;

    //Reference holder for the action that is being processed
    private static final int MAX_SUBJECT_LENGTH = 36;
    private static final String SUBJECT_SHORTENED_SUFFIX = "\u2026";
    private static final String TEXT_PLAIN = "text/plain";

    private static final int PENDING_MESSAGE_RETRY_COUNT = 3;
    private static final int PENDING_FRIENDREQUEST_RETRY_COUNT = 3;

    private static final int MAX_PAGE_LIMIT = 25;

    private static String actionInProgress = null;

    private static int callCount;
    private static String[] inProgressActionParams;

    private static final String EXTRA_UID = "com.bitlove.fetlife.extra.uid";

    private static final String EXTRA_CLEAR_UID = "com.bitlove.fetlife.extra.clear_uid";

    private static Map<String,String> clearUidsPerAction = new HashMap<>();

    /**
     * Main interaction method with this class to initiate a new Api call with the given parameters
     *
     * @param context The Android context
     * @param action The action matching with the desired Api call
     * @param params Parameters for the Api calls as Strings
     */
    public static synchronized void startApiCall(Context context, String action, String... params) {
        Intent intent = new Intent(context, FetLifeApiIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_PARAMS, params);
        intent.putExtra(EXTRA_UID, UUID.randomUUID().toString());
        enqueueWork(context,FetLifeApiIntentService.class,JOB_ID,intent);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(intent);
//        } else {
//            context.startService(intent);
//        }
    }

    public static synchronized void startClearApiCall(Context context, String action, String... params) {
        Intent intent = new Intent(context, FetLifeApiIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_PARAMS, params);
        String uid = UUID.randomUUID().toString();
        intent.putExtra(EXTRA_UID, uid);
        clearUidsPerAction.put(action,uid);
        enqueueWork(context,FetLifeApiIntentService.class,JOB_ID,intent);
    }

    public static synchronized Intent getActionIntent(Context context, String action, String... params) {
        Intent intent = new Intent(context, FetLifeApiIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_PARAMS, params);
        intent.putExtra(EXTRA_UID, UUID.randomUUID().toString());
        return intent;
    }

    public static synchronized void startPendingCalls(Context context, boolean checkForNotifications) {
        if (!isActionInProgress(ACTION_APICALL_SEND_MESSAGES)) {
            startApiCall(context, ACTION_APICALL_SEND_MESSAGES);
        }
        if (!isActionInProgress(ACTION_APICALL_SEND_GROUP_MESSAGES)) {
            startApiCall(context, ACTION_APICALL_SEND_GROUP_MESSAGES);
        }
        if (!isActionInProgress(ACTION_APICALL_PENDING_RELATIONS)) {
            startApiCall(context, ACTION_APICALL_PENDING_RELATIONS);
        }
        if (!isActionInProgress(ACTION_EXTERNAL_CALL_CHECK_4_UPDATES)) {
            startApiCall(context, ACTION_EXTERNAL_CALL_CHECK_4_UPDATES);
        }
        if (checkForNotifications && !isActionInProgress(ACTION_APICALL_NOTIFICATION_COUNTS)) {
            startApiCall(context, ACTION_APICALL_NOTIFICATION_COUNTS);
        }
        if (!isActionInProgress(ACTION_APICALL_CHECK_4_QUESTIONS)) {
            startApiCall(context, ACTION_APICALL_CHECK_4_QUESTIONS);
        }
    }

    //****
    //Methods for geting information about the currently handled request
    //****

    //TODO: think about being more specific and store also parameters for exact identification
    private static synchronized void setActionInProgress(String action) {
        actionInProgress = action;
    }

    private static void setInProgressActionParams(String[] params) {
        inProgressActionParams = params;
    }

    public static String[] getInProgressActionParams() {
        return inProgressActionParams;
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

    //Main synchronized method (by default by Intent Service implementation) method


    @Override
    public void onCreate() {
        super.onCreate();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForeground(1,new Notification());
//        }
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        callCount++;

        if (intent == null) {
            return;
        }

        //Set up the Api call related variables
        final String action = intent.getAction();

        String uId = intent.getStringExtra(EXTRA_UID);
        synchronized (FetLifeApiIntentService.class) {
            if (uId != null && clearUidsPerAction.containsKey(action)) {
                String clearUid = clearUidsPerAction.get(action);
                if (uId.equals(clearUid)) {
                    clearUidsPerAction.remove(action);
                } else {
                    return;
                }
            }
        }

        String[] params = intent.getStringArrayExtra(EXTRA_PARAMS);

        //Check current logged in user
        //Any communication with the Api is allowed only if the user is logged on, except of course the login process itself
        Member currentUser = getFetLifeApplication().getUserSessionManager().getCurrentUser();
        if (currentUser == null && action != ACTION_APICALL_LOGON_USER) {
            return;
        }

        //Check for network state
        if (NetworkUtil.getConnectivityStatus(this) == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            sendConnectionFailedNotification(action, params);
            return;
        }

        try {

            //Set the current action in progress and notify about whoever is interested
            setActionInProgress(action);
            setInProgressActionParams(params);
            sendLoadStartedNotification(action,params);

            //If we do not have any access token (for example because it is expired and removed) try to get new one with the stored refreshUi token
            if (action != ACTION_APICALL_LOGON_USER && getAccessToken() == null) {
                if (refreshToken(currentUser)) {
                    //If token successfully refreshed restart the original request
                    //Note: this could end up in endless loop if the backend keep sending invalid tokens, but at this point we assume backend works properly from this point of view
                    onHandleWork(intent);
                } else {
                    //Notify subscribers about failed authentication
                    sendAuthenticationFailedNotification();
                    return;
                }
            }

            //default success result of execution
            int result = Integer.MIN_VALUE;

            //Call the appropriate method based on the action to be executed
            switch (action) {
                case ACTION_APICALL_LOGON_USER:
                    result = logonUser(params);
                    break;
                case ACTION_APICALL_FEED:
                    result = retrieveFeed(params);
                    break;
                case ACTION_APICALL_EXPLORE:
                    result = retrieveExplore(params);
                    break;
                case ACTION_APICALL_CONVERSATIONS:
                    result = retrieveConversations(params);
                    break;
                case ACTION_APICALL_FRIENDS:
                    result = retrieveMyRelations(params);
                    break;
                case ACTION_APICALL_MEMBER_RELATIONS:
                    result = retrieveMemberRelations(params);
                    break;
                case ACTION_APICALL_MEMBER_PICTURES:
                    result = retrieveMemberPictures(params);
                    break;
                case ACTION_APICALL_MEMBER_VIDEOS:
                    result = retrieveMemberVideos(params);
                    break;
                case ACTION_APICALL_MEMBER_PICTURE:
                    result = getPicture(params);
                    break;
                case ACTION_APICALL_MEMBER_VIDEO:
                    result = getVideo(params);
                    break;
                case ACTION_APICALL_MEMBER_STATUSES:
                    result = retrieveMemberStatuses(params);
                    break;
                case ACTION_APICALL_MEMBER_EVENTS:
                    result = retrieveMemberEvents(params);
                    break;
                case ACTION_APICALL_MEMBER_WRITINGS:
                    result = retrieveMemberWritings(params);
                    break;
                case ACTION_APICALL_MEMBER_GROUPS:
                    result = retrieveMemberGroups(params);
                    break;
                case ACTION_APICALL_GROUP_MEMBERS:
                    result = retrieveGroupMembers(params);
                    break;
                case ACTION_APICALL_GROUP_DISCUSSIONS:
                    result = retrieveGroupDiscussions(params);
                    break;
                case ACTION_APICALL_WRITING:
                    result = getWriting(params);
                    break;
                case ACTION_APICALL_EVENT_RSVPS:
                    result = retrieveEventRsvps(params);
                    break;
                case ACTION_APICALL_MEMBER_FEED:
                    result = retrieveMemberFeed(params);
                    break;
                case ACTION_APICALL_CANCEL_FRIENDSHIP:
                    result = cancelFriendship(params);
                    break;
                case ACTION_APICALL_CANCEL_FRIENDREQUEST:
                    result = cancelFriendRequest(params);
                    break;
                case ACTION_APICALL_FRIENDREQUESTS:
                    result = retrieveFriendRequests(params);
                    break;
                case ACTION_APICALL_MESSAGES:
                    result = retrieveMessages(currentUser, params);
                    break;
                case ACTION_APICALL_GROUP_MESSAGES:
                    result = retrieveGroupMessages(currentUser, params);
                    break;
                case ACTION_APICALL_SEND_MESSAGES:
                    for (int i = PENDING_MESSAGE_RETRY_COUNT; i > 0; i--) {
                        result = sendPendingMessages(currentUser, 0);
                        if (result != Integer.MIN_VALUE) {
                            break;
                        }
                    }
                    break;
                case ACTION_APICALL_SEND_GROUP_MESSAGES:
                    for (int i = PENDING_MESSAGE_RETRY_COUNT; i > 0; i--) {
                        result = sendPendingGroupMessages(currentUser, 0);
                        if (result != Integer.MIN_VALUE) {
                            break;
                        }
                    }
                    break;
                case ACTION_APICALL_SET_MESSAGES_READ:
                    result = setMessagesRead(params);
                    break;
                case ACTION_APICALL_ADD_LOVE:
                    result = addLove(params);
                    break;
                case ACTION_APICALL_SET_RSVP_STATUS:
                    result = setRsvp(params);
                    break;
                case ACTION_APICALL_FOLLOW_DISCUSSION:
                    result = followDiscussion(params);
                    break;
                case ACTION_APICALL_UNFOLLOW_DISCUSSION:
                    result = unfollowDiscussion(params);
                    break;
                case ACTION_APICALL_GROUP_JOIN:
                    result = joinGroup(params);
                    break;
                case ACTION_APICALL_GROUP_LEAVE:
                    result = leaveGroup(params);
                    break;
                case ACTION_APICALL_REMOVE_LOVE:
                    result = removeLove(params);
                    break;
                case ACTION_APICALL_PENDING_RELATIONS:
                    result = sendPendingFriendRequests();
                    break;
                case ACTION_APICALL_CHECK_4_QUESTIONS:
                    result = sendCheck4QuestionsRequests();
                    break;
                case ACTION_APICALL_UPLOAD_PICTURE:
                    result = uploadPicture(params);
                    break;
                case ACTION_APICALL_UPLOAD_VIDEO:
                    result = uploadVideo(params);
                    break;
                case ACTION_APICALL_UPLOAD_VIDEO_CHUNK:
                    result = uploadVideoChunk(params);
                    break;
                case ACTION_CANCEL_UPLOAD_VIDEO_CHUNK:
                    cancelUploadVideoChunk(params);
                    result = 1;
                    break;
                case ACTION_APICALL_SEARCH_MEMBER:
                    result = searchMember(params);
                    break;
                case ACTION_APICALL_SEARCH_GROUP:
                    result = searchGroup(params);
                    break;
                case ACTION_APICALL_SEARCH_EVENT_BY_TAG:
                    result = searchEventByTag(params);
                    break;
                case ACTION_APICALL_SEARCH_EVENT_BY_LOCATION:
                    result = searchEventByLocation(params);
                    break;
                case ACTION_APICALL_MEMBER:
                    result = getMember(params);
                    break;
                case ACTION_EXTERNAL_CALL_CHECK_4_UPDATES:
                    result = checkForUpdates(params);
                    break;
                case ACTION_APICALL_EVENT:
                    result = getEvent(params);
                    break;
                case ACTION_APICALL_GROUP:
                    result = getGroup(params);
                    break;
                case ACTION_APICALL_NOTIFICATION_COUNTS:
                    result = getNotificationCounts(params);
                    break;
            }

            int lastResponseCode = getFetLifeApplication().getFetLifeService().getLastResponseCode();

            if (result == Integer.MIN_VALUE) {
                //If the call failed notify all subscribers about
//                Crashlytics.logException(new Exception("EXTRA LOG Load failed with response code: " + action + ";" + lastResponseCode));
                sendLoadFailedNotification(action, params);
            } else if (action != ACTION_APICALL_LOGON_USER && (lastResponseCode == 401)) {
                //If the result is failed due to Authentication or Authorization issue, let's try to refreshUi the token as it is most probably expired
                if (refreshToken(currentUser)) {
                    //If token refreshUi succeed restart the original request
                    //TODO think about if we can end up endless loop in here in case of not proper response from the backend.
                    onHandleWork(intent);
                } else {
                    //Notify subscribers about failed authentication
                    sendAuthenticationFailedNotification();
                }
                //TODO: error handling for endless loop
            } else if (result != Integer.MAX_VALUE) {
                //If the call succeed notify all subscribers about
                sendLoadFinishedNotification(action, result, params);
            }
        } catch (IOException ioe) {
            //If the call failed notify all subscribers about
//            Crashlytics.logException(new Exception("EXTRA LOG Connection failed with exception",ioe));
            sendConnectionFailedNotification(action, params);
        } catch (SQLiteDiskIOException | InvalidDBConfiguration | SQLiteReadOnlyDatabaseException | IllegalStateException idb) {
            //db might have been closed due probably to user logout, check it and let
            //the exception go in case of it is not the case
            //TODO: create separate DB Manager class to synchronize db executions and DB close due to user logout
            if (getFetLifeApplication().getUserSessionManager().getCurrentUser() != null) {
                throw idb;
            }
        } finally {
            //make sure we set the action in progress indicator correctly
            setActionInProgress(null);
            setInProgressActionParams(null);
        }
    }

    private int sendCheck4QuestionsRequests() throws IOException {
        Call<List<String>> sendCheck4QuestionsCall = getFetLifeApplication().getFetLifeService().getFetLifeApi().check4QuestionsFeature(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken());
        Response<List<String>> sendCheck4QuestionsResponse = sendCheck4QuestionsCall.execute();
        if (sendCheck4QuestionsResponse.isSuccessful()) {
            boolean questionsEnabled = (Collections.binarySearch(sendCheck4QuestionsResponse.body(),FEATURE_QUESTIONS) >= 0);
            getFetLifeApplication().getUserSessionManager().getActiveUserPreferences().edit().putBoolean(UserSessionManager.PREF_KEY_QUESTIONS_ENABLED,questionsEnabled).apply();
            return 0;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private int checkForUpdates(String... params) throws IOException {
        boolean forcedCheck = getBoolFromParams(params,0,false);
        Call<List<Release>> releasesCall = getFetLifeApplication().getGitHubService().getGitHubApi().getReleases();
        Response<List<Release>> releasesCallResponse = releasesCall.execute();
        if (releasesCallResponse.isSuccessful()) {
            Release latestRelease = null;
            Release latestPreRelease = null;
            final List<Release> releases = releasesCallResponse.body();
            Collections.sort(releases, new Comparator<Release>() {
                @Override
                public int compare(Release o1, Release o2) {
                    return VersionUtil.getVersionInt(o2.getTag())-VersionUtil.getVersionInt(o1.getTag());
                }
            });
            for (Release release : releases) {
                if (release.isPrerelease() && latestPreRelease == null) {
                    latestPreRelease = release;
                } else if (latestRelease == null) {
                    latestRelease = release;
                }
                if (latestRelease != null && latestPreRelease != null) {
                    break;
                }
            }
            getFetLifeApplication().getEventBus().post(new LatestReleaseEvent(latestRelease, latestPreRelease, forcedCheck));
            return Integer.MAX_VALUE;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    //****
    //Authentication related methods / Api calls
    //****

    //Special internal call for refreshing token using refreshUi token
    private boolean refreshToken(Member currentUser) throws IOException {

        if (currentUser == null) {
            if (BuildConfig.DEBUG) {
//                Debug.waitForDebugger();
            }
            return false;
        }

        String refreshToken = currentUser.getRefreshToken();

        if (refreshToken == null) {
            if (BuildConfig.DEBUG) {
//                Debug.waitForDebugger();
            }
            return false;
        }

        Call<Token> tokenRefreshCall = getFetLifeApplication().getFetLifeService().getFetLifeApi().refreshToken(
                BuildConfig.CLIENT_ID,
                getClientSecret(),
                BuildConfig.REDIRECT_URL,
                FetLifeService.GRANT_TYPE_TOKEN_REFRESH,
                refreshToken
        );

        Response<Token> tokenResponse = tokenRefreshCall.execute();

        if (tokenResponse.isSuccessful()) {
            //Set the new token information for the current user and mergeSave it to the db
            Token responseBody = tokenResponse.body();
            currentUser.setAccessToken(responseBody.getAccessToken());
            currentUser.setRefreshToken(responseBody.getRefreshToken());
            currentUser.mergeSave();
            return true;
        } else {
            return false;
        }
    }

    //Call for logging in the user
    private int logonUser(String... params) throws IOException {
        Call<Token> tokenCall = getFetLifeApplication().getFetLifeService().getFetLifeApi().login(
                BuildConfig.CLIENT_ID,
                getClientSecret(),
                BuildConfig.REDIRECT_URL,
                new AuthBody(params[0], params[1]));

        Response<Token> tokenResponse = tokenCall.execute();
        if (tokenResponse.isSuccessful()) {
            Token responseBody = tokenResponse.body();
            String accessToken = responseBody.getAccessToken();
            //Retrieve user information from the backend after Authentication
            Member user = retrieveCurrentUser(accessToken);
            if (user == null) {
                return Integer.MIN_VALUE;
            }
            //Save the user information with the tokens into the backend
            user.setAccessToken(accessToken);
            user.setRefreshToken(responseBody.getRefreshToken());

            //Notify the Session Manager about finished logon process
            getFetLifeApplication().getUserSessionManager().onUserLogIn(user, getBoolFromParams(params, 2, true));
            return 1;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    //Special internal call to retrieve user information after authentication
    private Member retrieveCurrentUser(String accessToken) throws IOException {
        Call<Member> getMeCall = getFetLifeApi().getMe(FetLifeService.AUTH_HEADER_PREFIX + accessToken);
        Response<Member> getMeResponse = getMeCall.execute();
        if (getMeResponse.isSuccessful()) {
            return getMeResponse.body();
        } else {
            return null;
        }
    }


    //****
    //Pending post request related methods / Api calls
    //****

    //Go through all the pending messages and send them one by one
    private int sendPendingMessages(Member user, int sentMessageCount) throws IOException {
        List<Message> pendingMessages = new Select().from(Message.class).where(Message_Table.pending.is(true)).orderBy(Message_Table.date,true).queryList();
        //Go through all pending messages (if there is any) and try to send them
        for (Message pendingMessage : pendingMessages) {
            String conversationId = pendingMessage.getConversationId();
            //If the conversation id is local (not created by the backend) it's a new conversation, so start a new conversation call
            if (Conversation.isLocal(conversationId)) {
                if (startNewConversation(user, conversationId, pendingMessage)) {
                    //db changed, reload remaining pending messages with starting this method recursively
                    return sendPendingMessages(user, ++sentMessageCount);
                } else {
                    continue;
                }
            } else if (sendPendingMessage(pendingMessage)) {
                MessageDuplicationDebugUtil.checkSentMessage(pendingMessage);
                sentMessageCount++;
            } else {
                continue;
            }
        }
        //Return success result if at least one pending message could have been sent so there was a change in the current state
        return sentMessageCount;
    }

    private boolean startNewConversation(Member user, String localConversationId, Message startMessage) throws IOException {

        Conversation pendingConversation = new Select().from(Conversation.class).where(Conversation_Table.id.is(localConversationId)).querySingle();
        if (pendingConversation == null) {
            return false;
        }

        String body = startMessage.getBody();
        String subject = body == null || body.length() <= MAX_SUBJECT_LENGTH ? body : body.substring(0,MAX_SUBJECT_LENGTH).concat(SUBJECT_SHORTENED_SUFFIX);
        Call<Conversation> postConversationCall = getFetLifeApi().postConversation(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), pendingConversation.getMemberId(), subject, body);
        Response<Conversation> postConversationResponse;
        try {
            postConversationResponse = postConversationCall.execute();
        } catch (IllegalArgumentException iae ) {
            //okio bug
            Crashlytics.logException(iae);
            postConversationResponse = null;
        }
        if (postConversationResponse != null && postConversationResponse.isSuccessful()) {
            //Delete the local conversation and create a new one, as the id of the conversation is changed (from local to backend based)
            pendingConversation.delete();

            Conversation conversation = postConversationResponse.body();
            conversation.save();

            String serverConversationId = conversation.getId();

            //Delete the temporary local start messages as it is now accessible via a backend call with its real backend related id
            //This will ensure we wont have any duplication
            startMessage.delete();
            //Retrieve the init message fot the conversation with the real backend id
            retrieveMessages(user, serverConversationId);

            //Update all other messages the user initiated in the meanwhile so they are not mapped to the new conversation
            //They will be now pending messages ready to be sent so a next scan (will be forced after returning from this method) will find them and send them
            List<Message> pendingMessages = new Select().from(Message.class).where(Message_Table.conversationId.is(localConversationId)).orderBy(Message_Table.date,true).queryList();
            for (Message pendingMessage : pendingMessages) {
                pendingMessage.setConversationId(serverConversationId);
                pendingMessage.save();
            }

            //Notify subscribers about new conversation event
            getFetLifeApplication().getEventBus().post(new NewConversationEvent(localConversationId, serverConversationId));
            return true;
        } else {
            int reason = getFetLifeApplication().getFetLifeService().getLastResponseCode();
            startMessage.setPending(false);
            startMessage.setFailed(true);
            startMessage.save();
            getFetLifeApplication().getEventBus().post(new MessageSendFailedEvent(startMessage.getConversationId(), reason == 403));
            return false;
        }
    }

    //Send one particular message

    private static long lastSentMessageTime;

    private boolean sendPendingMessage(Message pendingMessage) throws IOException {
        //Server can handle only one message in every second without adding the same date to it

        //Workaround for server issue as it does not handle millis
        while (System.currentTimeMillis()/1000 == lastSentMessageTime/1000) {}

        Call<Message> postMessagesCall = getFetLifeApi().postMessage(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), pendingMessage.getConversationId(), pendingMessage.getBody());

        Response<Message> postMessageResponse;
        try {
            postMessageResponse = postMessagesCall.execute();
        } catch (IOException ioe) {
            throw ioe;
        }

        String conversationId = pendingMessage.getConversationId();
        if (postMessageResponse.isSuccessful()) {
            lastSentMessageTime = System.currentTimeMillis();
            //Update the message state of the returned message object
            final Message message = postMessageResponse.body();
            //Messages are identify
            // ed in the db by client id so original pending message will be overridden here with the correct state
            message.setClientId(pendingMessage.getClientId());
            message.setPending(false);
            message.setConversationId(conversationId);
            message.update();
            getFetLifeApplication().getEventBus().post(new MessageSendSucceededEvent(conversationId));
            return true;
        } else {
            int reason = getFetLifeApplication().getFetLifeService().getLastResponseCode();
            //If the call failed make the pending message to a failed message
            //Note if the post is failed due to connection issue an exception will be thrown, so here we make the assumption the failure is permanent.
            //TODO check the result code and based on that send the message permanently failed or keep it still pending
            //TODO add functionality for the user to be able to retry sending failed messages
            pendingMessage.setPending(false);
            pendingMessage.setFailed(true);
            pendingMessage.save();
            getFetLifeApplication().getEventBus().post(new MessageSendFailedEvent(conversationId, reason == 403));
            return false;
        }
    }

    //Go through all the pending messages and send them one by one
    private int sendPendingGroupMessages(Member user, int sentMessageCount) throws IOException {
        List<GroupComment> pendingMessages = new Select().from(GroupComment.class).where(GroupComment_Table.pending.is(true)).orderBy(GroupComment_Table.date,true).queryList();
        //Go through all pending messages (if there is any) and try to send them
        for (GroupComment pendingMessage : pendingMessages) {
            String groupId = pendingMessage.getGroupId();
            String groupPostId = pendingMessage.getGroupPostId();
            //If the conversation id is local (not created by the backend) it's a new conversation, so start a new conversation call
            if (sendPendingGroupMessage(pendingMessage)) {
                sentMessageCount++;
            } else {
                continue;
            }
        }
        //Return success result if at least one pending message could have been sent so there was a change in the current state
        return sentMessageCount;
    }

    private static long lastSentGroupMessageTime;

    private boolean sendPendingGroupMessage(GroupComment pendingMessage) throws IOException {
        //Server can handle only one message in every second without adding the same date to it

        //Workaround for server issue as it does not handle millis
        while (System.currentTimeMillis()/1000 == lastSentGroupMessageTime/1000) {}

        Call<GroupComment> postMessagesCall = getFetLifeApi().postGroupMessage(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), pendingMessage.getGroupId(), pendingMessage.getGroupPostId(), pendingMessage.getBody());

        Response<GroupComment> postMessageResponse;
        try {
            postMessageResponse = postMessagesCall.execute();
        } catch (IOException ioe) {
            throw ioe;
        }

        String groupId = pendingMessage.getGroupId();
        String groupPostId = pendingMessage.getGroupPostId();
        if (postMessageResponse.isSuccessful()) {
            lastSentGroupMessageTime = System.currentTimeMillis();
            //Update the message state of the returned message object
            final GroupComment message = postMessageResponse.body();
            //Messages are identify
            // ed in the db by client id so original pending message will be overridden here with the correct state
            message.setClientId(pendingMessage.getClientId());
            message.setPending(false);
            message.setGroupId(groupId);
            message.setGroupPostId(groupPostId);
            message.update();
            getFetLifeApplication().getEventBus().post(new GroupMessageSendSucceededEvent(groupId,groupPostId));
            return true;
        } else {
            //If the call failed make the pending message to a failed message
            //Note if the post is failed due to connection issue an exception will be thrown, so here we make the assumption the failure is permanent.
            //TODO check the result code and based on that send the message permanently failed or keep it still pending
            //TODO add functionality for the user to be able to retry sending failed messages
            pendingMessage.setPending(false);
            pendingMessage.setFailed(true);
            pendingMessage.save();
            getFetLifeApplication().getEventBus().post(new GroupMessageSendFailedEvent(groupId,groupPostId));
            return false;
        }
    }

    private int sendPendingFriendRequests() throws IOException {

        int sentCount = 0;
        List<FriendRequest> pendingFriendRequests = new Select().from(FriendRequest.class).where(FriendRequest_Table.pending.is(true)).orderBy(FriendRequest_Table.id,true).queryList();
        for (FriendRequest pendingFriendRequest : pendingFriendRequests) {
            if (pendingFriendRequest.getPendingState() == FriendRequest.PendingState.OUTGOING) {
                if (!sendPendingFriendRequest(pendingFriendRequest)) {
                    if (pendingFriendRequest.getClientId() == null) {
                        new Delete().from(FriendRequest.class).where(FriendRequest_Table.targetMemberId.is(pendingFriendRequest.getTargetMemberId())).query();
                    } else {
                        pendingFriendRequest.delete();
                    }
                } else {
                    sentCount++;
                }
            } else {
                if (!sendPendingFriendRequestResponse(pendingFriendRequest)) {
                    pendingFriendRequest.delete();
                } else {
                    sentCount++;
                }
            }
        }
        List<SharedProfile> pendingSharedProfiles = new Select().from(SharedProfile.class).where(SharedProfile_Table.pending.is(true)).orderBy(SharedProfile_Table.memberId,true).queryList();
        for (SharedProfile pendingSharedProfile : pendingSharedProfiles) {
            if (!sendPendingSharedProfile(pendingSharedProfile)) {
                pendingSharedProfile.delete();
            } else {
                sentCount++;
            }
        }

        List<FollowRequest> followRequests = new Select().from(FollowRequest.class).queryList();
        for (FollowRequest followRequest : followRequests) {
            if (!sendFollowRequest(followRequest)) {
                followRequest.delete();
            } else {
                sentCount++;
            }
        }

        //TODO: check later if sending error counter would make any sense here
        return sentCount;
    }

    private boolean sendPendingFriendRequest(FriendRequest pendingFriendRequest) throws IOException {
        Call<FriendRequest> createFriendRequestCall = getFetLifeApi().createFriendRequest(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), pendingFriendRequest.getTargetMemberId());
        Response<FriendRequest> friendRequestResponse = createFriendRequestCall.execute();
        if (friendRequestResponse.isSuccessful()) {
            pendingFriendRequest.delete();
            return true;
        } else {
            return false;
        }
    }

    private boolean sendFollowRequest(FollowRequest followRequest) throws IOException {
        Call<ResponseBody> followCall = followRequest.isFollow() ? getFetLifeApi().createFollow(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), followRequest.getMemberId()) : getFetLifeApi().removeFollow(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), followRequest.getMemberId());
        Response<ResponseBody> followResponse = followCall.execute();
        if (followResponse.isSuccessful()) {
            followRequest.delete();
            return true;
        } else {
            return false;
        }
    }

    private boolean sendPendingSharedProfile(SharedProfile pendingSharedProfile) throws IOException {
        Call<FriendRequest> createFriendRequestCall = getFetLifeApi().createFriendRequest(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), pendingSharedProfile.getMemberId());
        Response<FriendRequest> friendRequestResponse = createFriendRequestCall.execute();
        if (friendRequestResponse.isSuccessful()) {
            pendingSharedProfile.delete();
            getFetLifeApplication().getEventBus().post(new FriendRequestResponseSendSucceededEvent());
            return true;
        } else {
            getFetLifeApplication().getEventBus().post(new FriendRequestResponseSendFailedEvent());
            return false;
        }
    }

    private boolean sendPendingFriendRequestResponse(FriendRequest pendingFriendRequest) throws IOException {
        Call<FriendRequest> friendRequestsCall;
        switch (pendingFriendRequest.getPendingState()) {
            case ACCEPTED:
                friendRequestsCall = getFetLifeApi().acceptFriendRequests(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), pendingFriendRequest.getId());
                break;
            case REJECTED:
                friendRequestsCall = getFetLifeApi().cancelFriendRequest(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), pendingFriendRequest.getId());
                break;
            default:
                return false;
        }

        Response<FriendRequest> friendRequestResponse = friendRequestsCall.execute();
        if (friendRequestResponse.isSuccessful()) {
            pendingFriendRequest.delete();
            getFetLifeApplication().getEventBus().post(new FriendRequestResponseSendSucceededEvent());
            return true;
        } else {
            pendingFriendRequest.delete();
            getFetLifeApplication().getEventBus().post(new FriendRequestResponseSendFailedEvent());
            return false;
        }
    }


    //****
    //Other not pending state based POST methods
    //****
    //TODO make these also pending in case of connection failed and retry later

    private int setMessagesRead(String[] params) throws IOException {
        String conversationId = params[0];
        String[] messageIds = Arrays.copyOfRange(params, 1, params.length);
        Call<ResponseBody> setMessagesReadCall = getFetLifeApi().setMessagesRead(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), conversationId, messageIds);
        Response<ResponseBody> response = setMessagesReadCall.execute();
        return response.isSuccessful() ? 1 : Integer.MIN_VALUE;
    }

    private int setRsvp(String[] params) throws IOException {
        String eventId = getLocalId(params[0]);
        if (eventId == null) {
            return Integer.MIN_VALUE;
        }
        String rsvp = params[1];
        Call<ResponseBody> setRsvpStatus = getFetLifeApi().putRsvp(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), eventId, rsvp);
        Response<ResponseBody> response = setRsvpStatus.execute();
        return response.isSuccessful() ? 1 : Integer.MIN_VALUE;
    }

    private int joinGroup(String[] params) throws IOException {
        String groupId = params[0];
        Call<ResponseBody> joinGroup = getFetLifeApi().joinGroup(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), groupId);
        Response<ResponseBody> response = joinGroup.execute();
        return response.isSuccessful() ? 1 : Integer.MIN_VALUE;
    }

    private int leaveGroup(String[] params) throws IOException {
        String groupId = params[0];
        Call<ResponseBody> joinGroup = getFetLifeApi().leaveGroup(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), groupId);
        Response<ResponseBody> response = joinGroup.execute();
        return response.isSuccessful() ? 1 : Integer.MIN_VALUE;
    }

    private int followDiscussion(String[] params) throws IOException {
        String groupId = params[0];
        String discussionId = params[1];
        Call<ResponseBody> followDiscussion = getFetLifeApi().followDiscussion(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), groupId, discussionId);
        Response<ResponseBody> response = followDiscussion.execute();
        return response.isSuccessful() ? 1 : Integer.MIN_VALUE;
    }

    private int unfollowDiscussion(String[] params) throws IOException {
        String groupId = params[0];
        String discussionId = params[1];
        Call<ResponseBody> unfollowDiscussion = getFetLifeApi().unfollowDiscussion(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), groupId, discussionId);
        Response<ResponseBody> response = unfollowDiscussion.execute();
        return response.isSuccessful() ? 1 : Integer.MIN_VALUE;
    }


    private int addLove(String[] params) throws IOException {
        String contentId = params[0];
        String contentType = params[1];
        Call<ResponseBody> addLoveCall = getFetLifeApi().putLove(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), contentId, contentType);
        Response<ResponseBody> response = addLoveCall.execute();
        return response.isSuccessful() ? 1 : Integer.MIN_VALUE;
    }

    private int removeLove(String[] params) throws IOException {
        String contentId = params[0];
        String contentType = params[1];
        Call<ResponseBody> removeLoveCall = getFetLifeApi().deleteLove(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), contentId, contentType);
        Response<ResponseBody> response = removeLoveCall.execute();
        return response.isSuccessful() ? 1 : Integer.MIN_VALUE;
    }

    //****
    //Multimedia (POST) related methods / Api calls
    //****

    private int uploadPicture(final String[] params) throws IOException {
        new Thread(new Runnable() {

            @Override
            public void run() {
                uploadPictureBackground(params);
            }
        }).start();
        return 0;
    }

    private int uploadPictureBackground(String[] params) {

        Uri uri = Uri.parse(params[0]);
        ContentResolver contentResolver = getFetLifeApplication().getContentResolver();

        boolean deleteAfterUpload = getBoolFromParams(params, 1, false);
        String caption = params[2];
        boolean friendsOnly = getBoolFromParams(params, 3, false);

        InputStream inputStream;
        String mimeType = getMimeType(uri, false, contentResolver);

        if (mimeType == null) {
            Crashlytics.logException(new Exception("Media type for file to upload not found"));
            if (deleteAfterUpload) {
                deleteUri(uri);
            }

            getFetLifeApplication().getEventBus().post(new ServiceCallFailedEvent(ACTION_APICALL_UPLOAD_PICTURE, false, params));
            return Integer.MIN_VALUE;
        }

        try {
            inputStream = contentResolver.openInputStream(uri);
        } catch (Exception e) {
            Crashlytics.logException(new Exception("Media file to upload not found", e));
            if (deleteAfterUpload) {
                deleteUri(uri);
            }
            getFetLifeApplication().getEventBus().post(new ServiceCallFailedEvent(ACTION_APICALL_UPLOAD_PICTURE, false, params));
            return Integer.MIN_VALUE;
        }

        try {

            RequestBody mediaBody = RequestBody.create(MediaType.parse(mimeType), BytesUtil.getBytes(inputStream));
            RequestBody isAvatarPart = RequestBody.create(MediaType.parse(TEXT_PLAIN), Boolean.toString(false));
            RequestBody friendsOnlyPart = RequestBody.create(MediaType.parse(TEXT_PLAIN), Boolean.toString(friendsOnly));
            RequestBody captionPart = RequestBody.create(MediaType.parse(TEXT_PLAIN), caption);
            RequestBody isFromUserPart = RequestBody.create(MediaType.parse(TEXT_PLAIN), Boolean.toString(true));

            Call<ResponseBody> uploadPictureCall = getFetLifeUploadApi().uploadPicture(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), mediaBody, isAvatarPart, friendsOnlyPart, captionPart, isFromUserPart);
            Response<ResponseBody> response = uploadPictureCall.execute();

            if (deleteAfterUpload) {
                deleteUri(uri);
            }

            if (response.isSuccessful()) {
                getFetLifeApplication().getEventBus().post(new ServiceCallFinishedEvent(ACTION_APICALL_UPLOAD_PICTURE, 1, params));
                return 1;
            } else {
                getFetLifeApplication().getEventBus().post(new ServiceCallFailedEvent(ACTION_APICALL_UPLOAD_PICTURE, false, params));
                return Integer.MIN_VALUE;
            }
        } catch (IOException ioe) {
            if (deleteAfterUpload) {
                deleteUri(uri);
            }
            getFetLifeApplication().getEventBus().post(new ServiceCallFailedEvent(ACTION_APICALL_UPLOAD_PICTURE, false, params));
            return Integer.MIN_VALUE;
        }
    }

    private static final Map<String, Object> videoChunkCancelRequestHolder = Collections.synchronizedMap(new HashMap<String, Object>());
    private static final int MAX_VIDEO_CHUNK_UPLOAD_RETRY = 3;
    public static final int VIDEO_UPLOAD_CHUNK_SIZE_MBYTES = 5;
    public static final int MAX_VIDEO_FILE_SIZE = 100;
    private static final int MBYTE_MULTIPLYER = 1024 * 1025;

    private int uploadVideo(String[] params) throws IOException {

        //TODO(VID): check http://www.androidhive.info/2014/12/android-uploading-camera-image-video-to-server-with-progress-bar/

        Uri uri = Uri.parse(params[0]);
        ContentResolver contentResolver = getFetLifeApplication().getContentResolver();

        boolean deleteAfterUpload = getBoolFromParams(params, 1, false);
        String title = params[2];
        String text = params[3];
        boolean friendsOnly = getBoolFromParams(params, 4, false);

        String mimeType = getMimeType(uri, true, contentResolver);

        if (mimeType == null) {
            Crashlytics.logException(new Exception("Media type for file to upload not found"));
            return Integer.MIN_VALUE;
        }

        Cursor cursor = contentResolver.query(uri,
                null, null, null, null);
        cursor.moveToFirst();
        long size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
        cursor.close();
        if (size > MAX_VIDEO_FILE_SIZE * MBYTE_MULTIPLYER) {
            getFetLifeApplication().getEventBus().post(new VideoUploadFailedEvent(true));
            if (deleteAfterUpload) {
                deleteUri(uri);
            }
            return Integer.MAX_VALUE;
        }

        Call<VideoUploadResult> uploadVideoStartCall = getFetLifeApi().uploadVideoStart(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), title, text, "video_android.mp4", friendsOnly, true);
        Response<VideoUploadResult> uploadVideoStartResponse = uploadVideoStartCall.execute();

        if (!uploadVideoStartResponse.isSuccessful()) {
            if (deleteAfterUpload) {
                deleteUri(uri);
            }
            return Integer.MIN_VALUE;
        }

        String videoUploadId = uploadVideoStartResponse.body().getId();
        if (videoUploadId == null) {
            if (deleteAfterUpload) {
                deleteUri(uri);
            }
            return Integer.MIN_VALUE;
        }

        String[] chunkUris = FileUtil.splitFile(getFetLifeApplication(), uri, videoUploadId, VIDEO_UPLOAD_CHUNK_SIZE_MBYTES * MBYTE_MULTIPLYER);

        if (deleteAfterUpload) {
            deleteUri(uri);
        }

        final String chunkCallParams[] = new String[chunkUris.length+5];
        chunkCallParams[0] = videoUploadId;
        chunkCallParams[1] = mimeType;
        chunkCallParams[2] = "" + 0;
        chunkCallParams[3] = "" + 0;
        chunkCallParams[4] = "" + size / 1024 / 1024;
        System.arraycopy(chunkUris, 0, chunkCallParams, 5, chunkUris.length);

        FetLifeApiIntentService.startApiCall(getFetLifeApplication(), ACTION_APICALL_UPLOAD_VIDEO_CHUNK, chunkCallParams);

        return 1;
    }

    private void deleteUri(Uri uri) {
        try {
            getContentResolver().delete(uri, null, null);
        } catch (IllegalArgumentException iae) {
            File contentFile = new File(uri.toString());
            if (contentFile.exists()) {
                contentFile.delete();
            }
        }
    }

    private int uploadVideoChunk(final String[] params) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadVideoChunkBackground(params);
            }
        }).start();
        return 0;
    }

    private int uploadVideoChunkBackground(final String[] params) {
        ContentResolver contentResolver = getFetLifeApplication().getContentResolver();

        String videoUploadId = params[0];

        String mimeType = params[1];
        int chunkToProcess = getIntFromParams(params, 2, 0);
        int timeoutFailCounter = getIntFromParams(params, 3, 0);
        int totalSizeIMBytes = getIntFromParams(params, 4, 0);

        String[] uris = new String[params.length -5];
        System.arraycopy(params, 5, uris, 0, uris.length);

        if (videoChunkCancelRequestHolder.remove(videoUploadId) != null) {
            getFetLifeApplication().getEventBus().post(new VideoChunkUploadCancelEvent(videoUploadId, true));
            return Integer.MAX_VALUE;
        }

        try {

            getFetLifeApplication().getEventBus().post(new VideoChunkUploadStartedEvent(videoUploadId, chunkToProcess+1, uris.length, totalSizeIMBytes, timeoutFailCounter));

            Uri uri = Uri.parse(uris[chunkToProcess]);

            InputStream inputStream;
            try {
                inputStream = contentResolver.openInputStream(uri);
            } catch (Exception e) {
                Crashlytics.logException(new Exception("Media file to upload not found", e));
                getFetLifeApplication().getEventBus().post(new VideoChunkUploadFailedEvent(videoUploadId, chunkToProcess+1, uris.length, false));
                return Integer.MAX_VALUE;
            }

            if (inputStream == null) {
                getFetLifeApplication().getEventBus().post(new VideoChunkUploadFailedEvent(videoUploadId, chunkToProcess+1, uris.length, false));
                return Integer.MAX_VALUE;
            }

            RequestBody mediaBody = RequestBody.create(MediaType.parse(mimeType), BytesUtil.getBytes(inputStream));
            RequestBody numberPart = RequestBody.create(MediaType.parse(TEXT_PLAIN), Integer.toString(chunkToProcess+1));

            Call<ResponseBody> uploadVideoPartCall = getFetLifeUploadApi().uploadVideoPart(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), videoUploadId, mediaBody, numberPart);
            Response<ResponseBody> uploadVideoPartResponse = uploadVideoPartCall.execute();

            if (!uploadVideoPartResponse.isSuccessful()) {
                for (int i = chunkToProcess; i < uris.length; i++) {
                    Uri chunkUri = Uri.parse(uris[i]);
                    deleteUri(chunkUri);
                }
                getFetLifeApplication().getEventBus().post(new VideoChunkUploadFailedEvent(videoUploadId, chunkToProcess+1, uris.length, false));
                return Integer.MAX_VALUE;
            } else {
                deleteUri(uri);
            }

            if (chunkToProcess == uris.length-1) {
                if (videoChunkCancelRequestHolder.remove(videoUploadId) != null) {
                    getFetLifeApplication().getEventBus().post(new VideoChunkUploadCancelEvent(videoUploadId, false));
                    return Integer.MAX_VALUE;
                }

                Call<ResponseBody> uploadVideoFinishCall = getFetLifeApi().uploadVideoFinish(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), videoUploadId);
                Response<ResponseBody> uploadVideoFinishResponse = uploadVideoFinishCall.execute();

                if (uploadVideoFinishResponse.isSuccessful()) {
                    getFetLifeApplication().getEventBus().post(new VideoChunkUploadFinishedEvent(videoUploadId, chunkToProcess+1, uris.length));
                    return chunkToProcess;
                } else {
                    getFetLifeApplication().getEventBus().post(new VideoChunkUploadFailedEvent(videoUploadId, chunkToProcess+1, uris.length, false));
                    return Integer.MAX_VALUE;
                }
            } else {

                params[2] = Integer.toString(chunkToProcess+1);
                params[3] = Integer.toString(0);
                FetLifeApiIntentService.startApiCall(getFetLifeApplication(), ACTION_APICALL_UPLOAD_VIDEO_CHUNK, params);
                getFetLifeApplication().getEventBus().post(new VideoChunkUploadFinishedEvent(videoUploadId, chunkToProcess+1, uris.length));
                return chunkToProcess;
            }
        } catch (IOException ioException) {
            if (timeoutFailCounter < MAX_VIDEO_CHUNK_UPLOAD_RETRY) {
                params[3] = Integer.toString(timeoutFailCounter+1);
                FetLifeApiIntentService.startApiCall(getFetLifeApplication(), ACTION_APICALL_UPLOAD_VIDEO_CHUNK, params);
                return 0;
            } else {
                Crashlytics.logException(new Exception("Video chunk upload failed after retries",ioException));
                getFetLifeApplication().getEventBus().post(new VideoChunkUploadFailedEvent(videoUploadId, chunkToProcess+1, uris.length, false));
                for (int i = chunkToProcess; i < uris.length; i++) {
                    Uri chunkUri = Uri.parse(uris[i]);
                    deleteUri(chunkUri);
                }
                //throw ioException;
                return Integer.MAX_VALUE;
            }
        }
    }

    private static void cancelUploadVideoChunk(String... params) {
        videoChunkCancelRequestHolder.put(params[0],new Object());
    }

    private static String getMimeType(Uri uri, boolean isVideo, ContentResolver contentResolver) {
        String mimeType;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            mimeType = contentResolver.getType(uri);
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        }

        if (mimeType == null || mimeType.trim().length() == 0) {
            //let's give a try
            Crashlytics.logException(new Exception("MimeType could not be read for image upload, falling back to image/jpeg"));
            mimeType = isVideo ? "video/mp4" : "image/jpeg";
        }

        return mimeType;
    }

    //****
    //Retrieve (GET) related methods / Api calls
    //****

    private int retrieveMessages(Member user, String... params) throws IOException {

        final String conversationId = params[0];

        final boolean loadNewMessages = getBoolFromParams(params, 1, true);

        Call<Conversation> getConversationCall = getFetLifeApi().getConversation(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), conversationId);
        Response<Conversation> conversationResponse = getConversationCall.execute();

        if (conversationResponse.isSuccessful()) {
            Conversation retrievedConversation = conversationResponse.body();

            retrievedConversation.getMember().mergeSave();

            Conversation localConversation = new Select().from(Conversation.class).where(Conversation_Table.id.is(conversationId)).querySingle();
            if (localConversation !=null) {
                retrievedConversation.setDraftMessage(localConversation.getDraftMessage());
            }
            retrievedConversation.save();
        } else {
            return Integer.MIN_VALUE;
        }

        Call<List<Message>> getMessagesCall = null;
        if (loadNewMessages) {
            String selfId = user.getId();
            Message newestMessage = new Select().from(Message.class).where(Message_Table.conversationId.is(conversationId)).and(Message_Table.senderId.isNot(selfId)).orderBy(Message_Table.date, false).querySingle();
            getMessagesCall = getFetLifeApi().getMessages(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), conversationId, newestMessage != null ? newestMessage.getId() : null, null, PARAM_NEWMESSAGE_LIMIT);
        } else {
            Message oldestMessage = new Select().from(Message.class).where(Message_Table.conversationId.is(conversationId)).and(Message_Table.pending.is(false)).orderBy(Message_Table.date,true).querySingle();
            getMessagesCall = getFetLifeApi().getMessages(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), conversationId, null, oldestMessage != null ? oldestMessage.getId() : null, PARAM_OLDMESSAGE_LIMIT);
        }

        //TODO solve edge case when there is the gap between last message in db and the retrieved messages (e.g. when because of the limit not all recent messages could be retrieved)

        Response<List<Message>> messagesResponse = getMessagesCall.execute();
        if (messagesResponse.isSuccessful()) {
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
                        message.setConversationId(conversationId);
                        message.setPending(false);
                        message.save();
                    }
                }
            });
            return messages.size();
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int retrieveGroupMessages(Member user, String... params) throws IOException {

        final String groupId = getLocalId(params[0]);
        final String groupDiscussionId = getLocalId(params[1]);

        final int limit = getIntFromParams(params, 2, 25);
        final int page = getIntFromParams(params, 3, 1);

        Call<GroupPost> getGroupDiscussionCall = getFetLifeApi().getGroupDiscussion(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), groupId, groupDiscussionId);
        Response<GroupPost> groupDiscussionResponse = getGroupDiscussionCall.execute();
        GroupPost retrievedGroupDiscussion;

        if (groupDiscussionResponse.isSuccessful()) {
            retrievedGroupDiscussion = groupDiscussionResponse.body();

            retrievedGroupDiscussion.getMember().mergeSave();

            GroupPost localGroupDiscussion = new Select().from(GroupPost.class).where(GroupPost_Table.id.is(groupDiscussionId)).querySingle();
            if (localGroupDiscussion !=null) {
                retrievedGroupDiscussion.setDraftMessage(localGroupDiscussion.getDraftMessage());
            }
            retrievedGroupDiscussion.save();
        } else {
            return Integer.MIN_VALUE;
        }

        Call<List<GroupComment>> getGroupMessagesCall = null;
        getGroupMessagesCall = getFetLifeApi().getGroupMessages(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), groupId, groupDiscussionId, limit, page);

        //TODO solve edge case when there is the gap between last message in db and the retrieved messages (e.g. when because of the limit not all recent messages could be retrieved)

        Response<List<GroupComment>> messagesResponse = getGroupMessagesCall.execute();
        if (messagesResponse.isSuccessful()) {
            final List<GroupComment> messages = messagesResponse.body();
            if (page == 1 && !messages.isEmpty()) {
                long messageDate = messages.get(0).getDate();
                Group group = Group.loadGroup(groupId);
                if (group != null && group.getDate() < messageDate) {
                    group.setDate(messageDate);
                    group.save();
                }
                GroupPost groupDiscussion = GroupPost.loadGroupPost(groupDiscussionId);
                if (groupDiscussionId != null && groupDiscussion.getDate() < messageDate) {
                    groupDiscussion.setDate(messageDate);
                    groupDiscussion.save();
                }
            }
            FlowManager.getDatabase(FetLifeDatabase.class).executeTransaction(new ITransaction() {
                @Override
                public void execute(DatabaseWrapper databaseWrapper) {
                    for (GroupComment message : messages) {
                        GroupComment storedMessage = new Select().from(GroupComment.class).where(GroupComment_Table.id.is(message.getId())).querySingle();
                        if (storedMessage != null) {
                            message.setClientId(storedMessage.getClientId());
                        } else {
                            message.setClientId(UUID.randomUUID().toString());
                        }
                        message.setGroupId(groupId);
                        message.setGroupPostId(groupDiscussionId);
                        message.setPending(false);
                        message.save();
                    }
                }
            });
            return messages.size();
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int retrieveFeed(String[] params) throws IOException {
        final int limit = getIntFromParams(params, 0, 25);
        final int page = getIntFromParams(params, 1, 1);

        Call<Feed> getFeedCall = getFetLifeApi().getFeed(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), limit, page);
//        Call<Feed> getFeedCall = getFetLifeApi().getFeed2(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken());
        Response<Feed> feedResponse = getFeedCall.execute();
        if (feedResponse.isSuccessful()) {
            final Feed feed = feedResponse.body();
            final List<Story> stories = feed.getStories();
            getFetLifeApplication().getInMemoryStorage().addFeed(page,stories);

//            Kept for later if/when we move to database persistence
//            FlowManager.getDatabase(FetLifeDatabase.class).executeTransaction(new ITransaction() {
//                @Override
//                public void execute(DatabaseWrapper databaseWrapper) {
//                    for (FeedStory story : stories) {
//                        story.mergeSave();
//                    }
//                }
//            });

            return stories.size();
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int retrieveExplore(String[] params) throws IOException {
        ExploreActivity.Explore exploreType = ExploreActivity.Explore.valueOf(params[0]);
        final int limit = getIntFromParams(params, 1, 25);
        final int page = getIntFromParams(params, 2, 1);

        Call<List<Story>> getFeedCall;
        List<Story> currentStories = getFetLifeApplication().getInMemoryStorage().getExploreFeed(exploreType);
        int lastStoryPos = (page-1)*limit-1;
        if (lastStoryPos >= currentStories.size()) {
            lastStoryPos = currentStories.size()-1;
        }
        String marker = null;
        do {
            Story lastStory = lastStoryPos >= 0 ? currentStories.get(lastStoryPos) : null;
            if (lastStory == null) break;
            FeedItemResourceHelper feedItemResourceHelper = new FeedItemResourceHelper(getFetLifeApplication(),lastStory);
            marker = feedItemResourceHelper.getServerTimeStamp(lastStory.getEvents().get(0));
            lastStoryPos--;
        } while (marker == null);
        if (marker != null) {
            marker = Long.toString(DateUtil.parseDate(marker)*1000l);
        }
        switch (exploreType) {
            case STUFF_YOU_LOVE:
                getFeedCall = getFetLifeApi().getStuffYouLove(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), null, limit, page);
                break;
            case KINKY_AND_POPULAR:
                getFeedCall = getFetLifeApi().getKinkyAndPopular(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), marker, limit, page);
                break;
            default:
            case FRESH_AND_PERVY:
                getFeedCall = getFetLifeApi().getFreshAndPervy(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), marker, limit, page);
                break;
        }
        Response<List<Story>> feedResponse = getFeedCall.execute();
        if (feedResponse.isSuccessful()) {
            final List<Story> stories = feedResponse.body();
            getFetLifeApplication().getInMemoryStorage().addExploreFeed(exploreType,page,stories);

//            Kept for later if/when we move to database persistence
//            FlowManager.getDatabase(FetLifeDatabase.class).executeTransaction(new ITransaction() {
//                @Override
//                public void execute(DatabaseWrapper databaseWrapper) {
//                    for (FeedStory story : stories) {
//                        story.mergeSave();
//                    }
//                }
//            });

            return stories.size();
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int retrieveMemberFeed(String[] params) throws IOException {
        String memberId = getLocalId(params[0]);
        if (memberId == null) {
            return Integer.MIN_VALUE;
        }
        final int limit = getIntFromParams(params, 1, 25);
        final int page = getIntFromParams(params, 2, 1);

        Call<Feed> getFeedCall = getFetLifeApi().getMemberFeed(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), memberId, limit, page);
        Response<Feed> feedResponse = getFeedCall.execute();
        if (feedResponse.isSuccessful()) {
            final Feed feed = feedResponse.body();
            final List<Story> stories = feed.getStories();
            getFetLifeApplication().getInMemoryStorage().addProfileFeed(page,stories);

            return stories.size();
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int retrieveConversations(String[] params) throws IOException {
        final int limit = getIntFromParams(params, 0, 25);
        final int page = getIntFromParams(params, 1, 1);

        Call<List<Conversation>> getConversationsCall = getFetLifeApi().getConversations(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), PARAM_SORT_ORDER_UPDATED_DESC, limit, page);
        Response<List<Conversation>> conversationsResponse = getConversationsCall.execute();
        if (conversationsResponse.isSuccessful()) {
            final List<Conversation> conversations = conversationsResponse.body();
            final List<Conversation> currentConversations = new Select().from(Conversation.class).orderBy(Conversation_Table.date,false).queryList();

            int lastConfirmedConversationPosition;
            if (page == 1) {
                lastConfirmedConversationPosition = -1;
            } else {
                lastConfirmedConversationPosition = loadLastSyncedPosition(SyncedPositionType.CONVERSATION,"");
            }
            int newItemCount = 0, deletedItemCount = 0;

            for (Conversation conversation : conversations) {

                conversation.getMember().mergeSave();

                int foundPos;
                for (foundPos = lastConfirmedConversationPosition+1; foundPos < currentConversations.size(); foundPos++) {
                    Conversation checkConversation = currentConversations.get(foundPos);
                    if (conversation.getId().equals(checkConversation.getId())) {
                        conversation.setDraftMessage(checkConversation.getDraftMessage());
                        break;
                    }
                }
                if (foundPos >= currentConversations.size()) {
                    newItemCount++;
                } else {
                    for (int i = lastConfirmedConversationPosition+1; i < foundPos; i++) {
                        Conversation notMatchedConversation = currentConversations.get(i);
                        if (Conversation.isUnanswered(notMatchedConversation.getId(),getFetLifeApplication())) {
                            lastConfirmedConversationPosition++;
                        } else {
                            notMatchedConversation.delete();
                            deletedItemCount++;
                        }
                    }
                    lastConfirmedConversationPosition = foundPos;
                }
                conversation.save();
            }

            saveLastSyncedPosition(SyncedPositionType.CONVERSATION,"",lastConfirmedConversationPosition+newItemCount);

            return conversations.size() - deletedItemCount;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private String getAccessToken() {
        Member currentUser = getFetLifeApplication().getUserSessionManager().getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return currentUser.getAccessToken();
    }

    private int getMember(String... params) throws IOException {
        String memberId = getLocalId(params[0]);
        if (memberId == null) {
            return Integer.MIN_VALUE;
        }

        Call<Member> getMemberCall = getFetLifeApi().getMember(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), memberId);
        Response<Member> getMemberResponse = getMemberCall.execute();
        if (getMemberResponse.isSuccessful()) {
            Member member = getMemberResponse.body();
            Call<List<Relationship>> getMemberRelationshipCall = getFetLifeApi().getMemberRelationship(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), memberId);
            Response<List<Relationship>> getMemberRelationshipResponse = getMemberRelationshipCall.execute();
            if (getMemberRelationshipResponse.isSuccessful()) {
                new Delete().from(Relationship.class).where(Relationship_Table.memberId.is(memberId)).query();
                List<Relationship> relationships = getMemberRelationshipResponse.body();
                for (Relationship relationship : relationships) {
                    Member targetMember = relationship.getTargetMember();
                    if (targetMember != null) {
                        targetMember.mergeSave();
                    }
                    relationship.setMemberId(memberId);
                    relationship.save();
                }
            }
            member.internalSave();
            return 1;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int getWriting(String... params) throws IOException {
        String writingId = getLocalId(params[0]);
        String memberId = getLocalId(params[1]);
        if (memberId == null || writingId == null) {
            return Integer.MIN_VALUE;
        }
        Call<Writing> getWritingCall = getFetLifeApi().getWriting(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), memberId, writingId);
        Response<Writing> getWritingResponse = getWritingCall.execute();
        if (getWritingResponse.isSuccessful()) {
            Writing writing = getWritingResponse.body();
            writing.detailSave();
            return 1;
        } else {
            return Integer.MIN_VALUE;
        }

    }

    private int getEvent(String... params) throws IOException {
        String eventId = getLocalId(params[0]);
        if (eventId == null) {
            return Integer.MIN_VALUE;
        }
        Call<Event> getEventCall = getFetLifeApi().getEvent(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), eventId);
        Response<Event> getEventResponse = getEventCall.execute();
        if (getEventResponse.isSuccessful()) {
            Event event = getEventResponse.body();
            event.setRsvpStatus(Rsvp.RsvpStatus.NO);
            event.save();
            Response<List<Rsvp>> rsvpsResponse = getFetLifeApi().getRsvps(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(),eventId).execute();
            if (rsvpsResponse.isSuccessful()) {
                List<Rsvp> rsvps = rsvpsResponse.body();
                if (rsvps.size() > 0) {
                    Rsvp rsvp = rsvps.get(0);
                    event.setRsvpStatus(rsvp.getRsvpStatus());
                    event.save();
                }
                return 1;
            } else {
                return Integer.MIN_VALUE;
            }
        } else {
            return Integer.MIN_VALUE;
        }

    }
    private int getPicture(String... params) throws IOException {
        String memberId = getLocalId(params[0]);
        String pictureId = getLocalId(params[1]);
        if (pictureId == null || memberId == null) {
            return Integer.MIN_VALUE;
        }
        Call<Picture> getPictureCall = getFetLifeApi().getMemberPicture(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), memberId, pictureId);
        Response<Picture> getPictureResponse = getPictureCall.execute();
        if (getPictureResponse.isSuccessful()) {
            Picture picture = getPictureResponse.body();
            picture.save();
            return 1;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int getVideo(String... params) throws IOException {
        String memberId = getLocalId(params[0]);
        String videoId = getLocalId(params[1]);
        if (videoId == null || memberId == null) {
            return Integer.MIN_VALUE;
        }
        Call<Video> getVideoCall = getFetLifeApi().getMemberVideo(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), memberId, videoId);
        Response<Video> getVideoResponse = getVideoCall.execute();
        if (getVideoResponse.isSuccessful()) {
            Video video = getVideoResponse.body();
            video.save();
            return 1;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int getGroup(String... params) throws IOException {
        String groupId = getLocalId(params[0]);
        Call<Group> getGroupCall = getFetLifeApi().getGroup(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), groupId);
        Response<Group> getGroupResponse = getGroupCall.execute();
        if (getGroupResponse.isSuccessful()) {
            Group group = getGroupResponse.body();
            group.setDetailLoaded(true);
            group.save();
            return 1;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int getNotificationCounts(String... params) throws IOException {
        Call<NotificationCounts> getNotifCountCall = getFetLifeApi().getNotificationCounts(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken());
        Response<NotificationCounts> getNotifCountResponse = getNotifCountCall.execute();
        if (getNotifCountResponse.isSuccessful()) {
            NotificationCounts notifCounts = getNotifCountResponse.body();
            SharedPreferences preferences = getFetLifeApplication().getUserSessionManager().getActiveUserPreferences();
            preferences.edit()
                    .putInt(UserSessionManager.PREF_KEY_MESSAGE_COUNT,notifCounts.getMessageCount())
                    .putInt(UserSessionManager.PREF_KEY_REQUEST_COUNT,notifCounts.getRequestCount())
                    .putInt(UserSessionManager.PREF_KEY_NOTIF_COUNT,notifCounts.getNotificationCount())
                    .apply();
            getFetLifeApplication().getEventBus().post(new NotificationCountUpdatedEvent(notifCounts.getNotificationCount(), notifCounts.getRequestCount(), notifCounts.getMessageCount()));
            return Integer.MAX_VALUE;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int searchMember(String... params) throws IOException {
        final String queryString = params[0];
        final int limit = getIntFromParams(params, 1, 10);
        final int page = getIntFromParams(params, 2, 1);

        Response<List<Member>> relationsResponse = getFetLifeApi().searchMembers(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(),queryString,limit,page).execute();
        if (relationsResponse.isSuccessful()) {
            final List<Member> foundMembers = relationsResponse.body();
            for (Member friendMember : foundMembers) {
                friendMember.mergeSave();
            }
            return foundMembers.size();
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int searchGroup(String... params) throws IOException {
        final String queryString = params[0];
        final int limit = getIntFromParams(params, 1, 10);
        final int page = getIntFromParams(params, 2, 1);

        Response<List<Group>> searchResponse = getFetLifeApi().searchGroups(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(),queryString,limit,page).execute();
        if (searchResponse.isSuccessful()) {
            final List<Group> foundGroups = searchResponse.body();
            for (Group foundGroup : foundGroups) {
                foundGroup.save();
            }
            return foundGroups.size();
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int searchEventByTag(String... params) throws IOException {
        final String queryString = params[0];
        final int limit = getIntFromParams(params, 1, 10);
        final int page = getIntFromParams(params, 2, 1);

        Response<List<Event>> relationsResponse = getFetLifeApi().searchEvents(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(),queryString,limit,page).execute();
        if (relationsResponse.isSuccessful()) {
            final List<Event> foundEvents = relationsResponse.body();
            for (Event event : foundEvents) {
                event.save();
            }
            return foundEvents.size();
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int searchEventByLocation(String... params) throws IOException {

        final double swLatitude = Double.parseDouble(params[0]);
        final double swLongitude = Double.parseDouble(params[1]);
        final double neLatitude = Double.parseDouble(params[2]);
        final double neLongitude = Double.parseDouble(params[3]);
        final int limit = getIntFromParams(params, 4, 42);
        final int page = getIntFromParams(params, 5, 1);


        LatLngBounds searchBounds = new LatLngBounds(new LatLng(swLatitude,swLongitude),new LatLng(neLatitude,neLongitude));
        LatLng center = searchBounds.getCenter();
        double range = MapUtil.getRange(searchBounds.southwest,center);

        Response<List<Event>> relationsResponse = getFetLifeApi().searchEvents(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(),center.latitude,center.longitude,range,limit,page).execute();
        if (relationsResponse.isSuccessful()) {
            final List<Event> foundEvents = relationsResponse.body();
            getFetLifeApplication().getEventBus().post(new EventsByLocationRetrievedEvent(searchBounds,page,foundEvents));
//            Crashlytics.logException(new Exception("EXTRA LOG event search request succeeded"));
        } else {
//            Crashlytics.logException(new Exception("EXTRA LOG event search request failed:" + getFetLifeApplication().getFetLifeService().getLastResponseCode()));
            getFetLifeApplication().getEventBus().post(new EventsByLocationRetrieveFailedEvent(searchBounds,page));
        }
        return Integer.MAX_VALUE;
    }

    private int retrieveMyRelations(String[] params) throws IOException {
        String currentUserId = getFetLifeApplication().getUserSessionManager().getCurrentUser().getId();
        int relationType = getIntFromParams(params, 0, RelationReference.VALUE_RELATIONTYPE_FRIEND);
        String newParams[] = new String[params.length-1];
        System.arraycopy(params, 1, newParams, 0, params.length-1);
        return retrieveRelations(true, currentUserId, relationType, newParams);
    }

    private int retrieveMemberRelations(String[] params) throws IOException {
        String memberId = getLocalId(params[0]);
        if (memberId == null) {
            return Integer.MIN_VALUE;
        }
        int relationType = getIntFromParams(params, 1, RelationReference.VALUE_RELATIONTYPE_FRIEND);
        String newParams[] = new String[params.length-2];

        System.arraycopy(params, 2, newParams, 0, params.length-2);

        return retrieveRelations(false, memberId, relationType, newParams);
    }

    private int retrieveRelations(boolean myRelations, String userId, int relationType, String[] params) throws IOException {
        final int limit = getIntFromParams(params, 0, 10);
        final int page = getIntFromParams(params, 1, 1);

        final Call<List<Member>> getRelationsCall;
        final SyncedPositionType syncedPositionType;
        switch (relationType) {
            case RelationReference.VALUE_RELATIONTYPE_FRIEND:
                syncedPositionType = SyncedPositionType.FRIEND;
                if (myRelations) {
                    getRelationsCall = getFetLifeApi().getFriends(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), limit, page);
                } else {
                    getRelationsCall = getFetLifeApi().getMemberFriends(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), userId, limit, page);
                }
                break;
            case RelationReference.VALUE_RELATIONTYPE_FOLLOWER:
                syncedPositionType = SyncedPositionType.FOLLOWER;
                if (myRelations) {
                    throw new IllegalAccessError("Not yet Implemented");
                } else {
                    getRelationsCall = getFetLifeApi().getMemberFollowers(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), userId, limit, page);
                }
                break;
            case RelationReference.VALUE_RELATIONTYPE_FOLLOWING:
                syncedPositionType = SyncedPositionType.FOLLOWING;
                if (myRelations) {
                    throw new IllegalAccessError("Not yet Implemented");
                } else {
                    getRelationsCall = getFetLifeApi().getMemberFollowees(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), userId, limit, page);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid RelationType");
        }

        Response<List<Member>> relationsResponse = getRelationsCall.execute();
        if (relationsResponse.isSuccessful()) {

            final List<Member> friendMembers = relationsResponse.body();
            List<RelationReference> currentFriends = new Select().from(RelationReference.class).where(RelationReference_Table.userId.is(userId)).and(RelationReference_Table.relationType.is(relationType)).orderBy(OrderBy.fromProperty(RelationReference_Table.nickname).ascending().collate(Collate.NOCASE)).queryList();
            final Collator coll = Collator.getInstance(Locale.US);
            coll.setStrength(Collator.IDENTICAL);
            Collections.sort(currentFriends, new Comparator<RelationReference>() {
                @Override
                public int compare(RelationReference relationReference, RelationReference relationReference2) {
                    //Workaround to match with DB sorting
                    String nickname1 = relationReference.getNickname().replaceAll("_","z");
                    String nickname2 = relationReference2.getNickname().replaceAll("_","z");
                    return coll.compare(nickname1,nickname2);
                }
            });

            int lastConfirmedFriendPosition;
            if (page == 1) {
                lastConfirmedFriendPosition = -1;
            } else {
                lastConfirmedFriendPosition = loadLastSyncedPosition(syncedPositionType,userId);
            }
            int newItemCount = 0, deletedItemCount = 0;

            for (Member friendMember : friendMembers) {

                friendMember.mergeSave();

                int foundPos;
                for (foundPos = lastConfirmedFriendPosition+1; foundPos < currentFriends.size(); foundPos++) {
                    RelationReference checkFriend = currentFriends.get(foundPos);
                    if (friendMember.getId().equals(checkFriend.getId())) {
                        break;
                    }
                }
                if (foundPos >= currentFriends.size()) {
                    newItemCount++;
                    RelationReference relationReference = new RelationReference();
                    relationReference.setId(friendMember.getId());
                    relationReference.setRelationType(relationType);
                    relationReference.setNickname(friendMember.getNickname());
                    relationReference.setUserId(userId);
                    relationReference.save();
                } else {
                    for (int i = lastConfirmedFriendPosition+1; i < foundPos; i++) {
                        currentFriends.get(i).delete();
                        deletedItemCount++;
                    }
                    lastConfirmedFriendPosition = foundPos;
                }
            }

            saveLastSyncedPosition(syncedPositionType,userId,lastConfirmedFriendPosition+newItemCount);

            return friendMembers.size() - deletedItemCount;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int retrieveGroupMembers(String[] params) throws IOException {
        String groupId = params[0];
        final int limit = getIntFromParams(params, 1, 10);
        final int page = getIntFromParams(params, 2, 1);

        final Call<List<GroupMembership>> getGroupMembersCall = getFetLifeApi().getGroupMembers(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), groupId, limit, page);
        final SyncedPositionType syncedPositionType = SyncedPositionType.GROUP_MEMBERS;

        Response<List<GroupMembership>> groupMembersResponse = getGroupMembersCall.execute();
        if (groupMembersResponse.isSuccessful()) {

            List<GroupMembership> memberships = groupMembersResponse.body();
            List<GroupMembershipReference> currentGroupMembers = new Select().from(GroupMembershipReference.class).where(GroupMembershipReference_Table.groupId.is(groupId)).orderBy(OrderBy.fromProperty(GroupMembershipReference_Table.createdAt).descending().collate(Collate.NOCASE)).queryList();

            int lastConfirmedPosition;
            if (page == 1) {
                lastConfirmedPosition = -1;
            } else {
                lastConfirmedPosition = loadLastSyncedPosition(syncedPositionType,groupId);
            }
            int newItemCount = 0, deletedItemCount = 0;

            for (GroupMembership groupMembership : memberships) {

                Member groupMember = groupMembership.getMember();
                groupMember.mergeSave();

                int foundPos;
                for (foundPos = lastConfirmedPosition+1; foundPos < currentGroupMembers.size(); foundPos++) {
                    GroupMembershipReference checkGroupMember = currentGroupMembers.get(foundPos);
                    if (groupMember.getId().equals(checkGroupMember.getId())) {
                        break;
                    }
                }
                if (foundPos >= currentGroupMembers.size()) {
                    newItemCount++;
                    GroupMembershipReference groupMembershipReference = new GroupMembershipReference();
                    groupMembershipReference.setId(groupMembership.getId());
                    groupMembershipReference.setMemberId(groupMember.getId());
                    groupMembershipReference.setNickname(groupMember.getNickname());
                    groupMembershipReference.setGroupId(groupId);
                    groupMembershipReference.setCreatedAt(groupMembership.getCreatedAt());
                    groupMembershipReference.setLastVisitedAt(groupMembership.getLastVisitedAt());
                    groupMembershipReference.save();
                } else {
                    for (int i = lastConfirmedPosition+1; i < foundPos; i++) {
                        //currentGroupMembers.get(i).delete();
                        deletedItemCount++;
                    }
                    lastConfirmedPosition = foundPos;
                }
            }

            saveLastSyncedPosition(syncedPositionType,groupId,lastConfirmedPosition+newItemCount);

            return memberships.size() - deletedItemCount;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int retrieveGroupDiscussions(String[] params) throws IOException {
        String groupId = params[0];
        final int limit = getIntFromParams(params, 1, 10);
        final int page = getIntFromParams(params, 2, 1);

        final Call<List<GroupPost>> getGroupDiscussionsCall = getFetLifeApi().getGroupDiscussions(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), groupId, limit, page);
        final SyncedPositionType syncedPositionType = SyncedPositionType.GROUP_DISCUSSIONS;

        Response<List<GroupPost>> groupDiscussionsResponse = getGroupDiscussionsCall.execute();
        if (groupDiscussionsResponse.isSuccessful()) {

            final List<GroupPost> groupDisucssions = groupDiscussionsResponse.body();
            List<GroupDiscussionReference> currentGroupDiscussions = new Select().from(GroupDiscussionReference.class).where(GroupDiscussionReference_Table.groupId.is(groupId)).orderBy(OrderBy.fromProperty(GroupDiscussionReference_Table.createdAt).ascending().collate(Collate.NOCASE)).queryList();
            final Collator coll = Collator.getInstance(Locale.US);
            coll.setStrength(Collator.IDENTICAL);

            int lastConfirmedPosition;
            if (page == 1) {
                lastConfirmedPosition = -1;
            } else {
                lastConfirmedPosition = loadLastSyncedPosition(syncedPositionType,groupId);
            }
            int newItemCount = 0, deletedItemCount = 0;

            for (GroupPost groupDiscussion : groupDisucssions) {

                GroupPost currentGroupDiscussion = GroupPost.loadGroupPost(groupDiscussion.getId());
                if (currentGroupDiscussion != null && currentGroupDiscussion.getDate() > groupDiscussion.getDate()) {
                    groupDiscussion.setDate(currentGroupDiscussion.getDate());
                }
                groupDiscussion.save();

                int foundPos;
                for (foundPos = lastConfirmedPosition+1; foundPos < currentGroupDiscussions.size(); foundPos++) {
                    GroupDiscussionReference checkGroupDiscussion = currentGroupDiscussions.get(foundPos);
                    if (checkGroupDiscussion.getId().equals(checkGroupDiscussion.getId())) {
                        break;
                    }
                }
                if (foundPos >= currentGroupDiscussions.size()) {
                    newItemCount++;
                    GroupDiscussionReference groupDiscussionReference = new GroupDiscussionReference();
                    groupDiscussionReference.setId(groupDiscussion.getId());
                    groupDiscussionReference.setCreatedAt(groupDiscussion.getCreatedAt());
                    groupDiscussionReference.setGroupId(groupId);
                    groupDiscussionReference.save();
                } else {
                    for (int i = lastConfirmedPosition+1; i < foundPos; i++) {
                        currentGroupDiscussions.get(i).delete();
                        deletedItemCount++;
                    }
                    lastConfirmedPosition = foundPos;
                }
            }

            saveLastSyncedPosition(syncedPositionType,groupId,lastConfirmedPosition+newItemCount);

            return groupDisucssions.size() - deletedItemCount;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int retrieveMyPictures(String[] params) throws IOException {
        String currentUserId = getFetLifeApplication().getUserSessionManager().getCurrentUser().getId();
        return retrievePictures(true, currentUserId, params);
    }

    private int retrieveMemberPictures(String[] params) throws IOException {
        String memberId = getLocalId(params[0]);
        if (memberId == null) {
            return Integer.MIN_VALUE;
        }
        String newParams[] = new String[params.length-1];

        System.arraycopy(params, 1, newParams, 0, params.length-1);

        return retrievePictures(false, memberId, newParams);
    }

    private int retrievePictures(boolean myPictures, String userId, String[] params) throws IOException {
        final int limit = getIntFromParams(params, 0, 10);
        final int page = getIntFromParams(params, 1, 1);

        final Call<List<Picture>> getPicturesCall;
        if (myPictures) {
            throw new NoSuchMethodError("Not Yet implemented");
        } else {
            getPicturesCall = getFetLifeApi().getMemberPictures(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), userId, limit, page);
        }

        Response<List<Picture>> picturesResponse = getPicturesCall.execute();
        if (picturesResponse.isSuccessful()) {

            final List<Picture> retrievedPictures = picturesResponse.body();
            List<PictureReference> currentPictures = new Select().from(PictureReference.class).where(PictureReference_Table.userId.is(userId)).orderBy(OrderBy.fromProperty(PictureReference_Table.date).descending()).queryList();

            int lastConfirmedPicturePosition;
            if (page == 1) {
                lastConfirmedPicturePosition = -1;
            } else {
                lastConfirmedPicturePosition = loadLastSyncedPosition(SyncedPositionType.PICTURE,userId);
            }
            int newItemCount = 0, deletedItemCount = 0;

            for (Picture retrievedPicture : retrievedPictures) {
                int foundPos;
                for (foundPos = lastConfirmedPicturePosition+1; foundPos < currentPictures.size(); foundPos++) {
                    PictureReference checkPicture = currentPictures.get(foundPos);
                    if (retrievedPicture.getId().equals(checkPicture.getId())) {
                        break;
                    }
                }
                if (foundPos >= currentPictures.size()) {
                    newItemCount++;
                    PictureReference pictureReference = new PictureReference();
                    pictureReference.setId(retrievedPicture.getId());
                    pictureReference.setCreatedAt(retrievedPicture.getCreatedAt());
                    pictureReference.setUserId(userId);
                    pictureReference.save();
                } else {
                    for (int i = lastConfirmedPicturePosition+1; i < foundPos; i++) {
                        currentPictures.get(i).delete();
                        deletedItemCount++;
                    }
                    lastConfirmedPicturePosition = foundPos;
                }
                retrievedPicture.save();
            }

            saveLastSyncedPosition(SyncedPositionType.PICTURE,userId,lastConfirmedPicturePosition+newItemCount);

            return retrievedPictures.size() - deletedItemCount;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int retrieveMyVideos(String[] params) throws IOException {
        String currentUserId = getFetLifeApplication().getUserSessionManager().getCurrentUser().getId();
        return retrieveVideos(true, currentUserId, params);
    }

    private int retrieveMemberVideos(String[] params) throws IOException {
        String memberId = getLocalId(params[0]);
        if (memberId == null) {
            return Integer.MIN_VALUE;
        }
        String newParams[] = new String[params.length-1];

        System.arraycopy(params, 1, newParams, 0, params.length-1);

        return retrieveVideos(false, memberId, newParams);
    }

    private int retrieveVideos(boolean myVideos, String userId, String[] params) throws IOException {
        final int limit = getIntFromParams(params, 0, 10);
        final int page = getIntFromParams(params, 1, 1);

        final Call<List<Video>> getVideosCall;
        if (myVideos) {
            throw new NoSuchMethodError("Not Yet implemented");
        } else {
            getVideosCall = getFetLifeApi().getMemberVideos(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), userId, limit, page);
        }

        Response<List<Video>> videosResponse = getVideosCall.execute();
        if (videosResponse.isSuccessful()) {

            final List<Video> retrievedVideos = videosResponse.body();
            List<VideoReference> currentVideos = new Select().from(VideoReference.class).where(VideoReference_Table.userId.is(userId)).orderBy(OrderBy.fromProperty(Picture_Table.date).descending()).queryList();

            int lastConfirmedVideoPosition;
            if (page == 1) {
                lastConfirmedVideoPosition = -1;
            } else {
                lastConfirmedVideoPosition = loadLastSyncedPosition(SyncedPositionType.VIDEO,userId);
            }
            int newItemCount = 0, deletedItemCount = 0;

            for (Video retrievedVideo : retrievedVideos) {
                int foundPos;
                for (foundPos = lastConfirmedVideoPosition+1; foundPos < currentVideos.size(); foundPos++) {
                    VideoReference checkVideo = currentVideos.get(foundPos);
                    if (retrievedVideo.getId().equals(checkVideo.getId())) {
                        break;
                    }
                }
                if (foundPos >= currentVideos.size()) {
                    newItemCount++;
                    VideoReference videoReference = new VideoReference();
                    videoReference.setId(retrievedVideo.getId());
                    videoReference.setCreatedAt(retrievedVideo.getCreatedAt());
                    videoReference.setUserId(userId);
                    videoReference.save();
                } else {
                    for (int i = lastConfirmedVideoPosition+1; i < foundPos; i++) {
                        currentVideos.get(i).delete();
                        deletedItemCount++;
                    }
                    lastConfirmedVideoPosition = foundPos;
                }
                retrievedVideo.save();
            }

            saveLastSyncedPosition(SyncedPositionType.VIDEO,userId,lastConfirmedVideoPosition+newItemCount);

            return retrievedVideos.size() - deletedItemCount;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int retrieveMyStatuses(String[] params) throws IOException {
        String currentUserId = getFetLifeApplication().getUserSessionManager().getCurrentUser().getId();
        return retrieveStatuses(true, currentUserId, params);
    }

    private int retrieveMemberStatuses(String[] params) throws IOException {
        String memberId = getLocalId(params[0]);
        if (memberId == null) {
            return Integer.MIN_VALUE;
        }
        String newParams[] = new String[params.length-1];

        System.arraycopy(params, 1, newParams, 0, params.length-1);

        return retrieveStatuses(false, memberId, newParams);
    }

    private int retrieveStatuses(boolean myStatus, String userId, String[] params) throws IOException {
        final int limit = getIntFromParams(params, 0, 10);
        final int page = getIntFromParams(params, 1, 1);

        final Call<List<Status>> getStatusCall;
        if (myStatus) {
            throw new NoSuchMethodError("Not Yet implemented");
        } else {
            getStatusCall = getFetLifeApi().getMemberStatuses(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), userId, limit, page);
        }

        Response<List<Status>> statussResponse = getStatusCall.execute();
        if (statussResponse.isSuccessful()) {

            final List<Status> retrievedStatuses = statussResponse.body();
            List<StatusReference> currentStatus = new Select().from(StatusReference.class).where(StatusReference_Table.userId.is(userId)).orderBy(OrderBy.fromProperty(Picture_Table.date).descending()).queryList();

            int lastConfirmedStatusPosition;
            if (page == 1) {
                lastConfirmedStatusPosition = -1;
            } else {
                lastConfirmedStatusPosition = loadLastSyncedPosition(SyncedPositionType.STATUS,userId);
            }
            int newItemCount = 0, deletedItemCount = 0;

            for (Status retrievedStatus : retrievedStatuses) {
                int foundPos;
                for (foundPos = lastConfirmedStatusPosition+1; foundPos < currentStatus.size(); foundPos++) {
                    StatusReference checkStatus = currentStatus.get(foundPos);
                    if (retrievedStatus.getId().equals(checkStatus.getId())) {
                        break;
                    }
                }
                if (foundPos >= currentStatus.size()) {
                    newItemCount++;
                    StatusReference statusReference = new StatusReference();
                    statusReference.setId(retrievedStatus.getId());
                    statusReference.setCreatedAt(retrievedStatus.getCreatedAt());
                    statusReference.setUserId(userId);
                    statusReference.save();
                } else {
                    for (int i = lastConfirmedStatusPosition+1; i < foundPos; i++) {
                        currentStatus.get(i).delete();
                        deletedItemCount++;
                    }
                    lastConfirmedStatusPosition = foundPos;
                }
                retrievedStatus.save();
            }

            saveLastSyncedPosition(SyncedPositionType.STATUS,userId,lastConfirmedStatusPosition+newItemCount);

            return retrievedStatuses.size() - deletedItemCount;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int retrieveEventRsvps(String[] params) throws IOException {
        String eventId = params[0];
        final int limit = getIntFromParams(params, 2, 10);
        final int page = getIntFromParams(params, 3, 1);
        final int rsvpType = getIntFromParams(params, 1, EventRsvpReference.VALUE_RSVPTYPE_GOING);

        final Call<List<Rsvp>> getRsvpsCall;
        getRsvpsCall = getFetLifeApi().getEventRsvps(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), eventId, EventRsvpReference.VALUE_RSVPTYPE_GOING == rsvpType ? PARAM_VALUE_EVENT_RSVP_YES : PARAM_VALUE_EVENT_RSVP_MAYBE,/*"start_date_time",*/ limit, page);

        Response<List<Rsvp>> rsvpsResponse = getRsvpsCall.execute();
        if (rsvpsResponse.isSuccessful()) {

            final List<Rsvp> retrievedRsvps = rsvpsResponse.body();
            List<EventRsvpReference> currentEventRsvps = new Select().from(EventRsvpReference.class).where(EventRsvpReference_Table.eventId.is(eventId)).orderBy(OrderBy.fromProperty(EventRsvpReference_Table.nickname).ascending()).queryList();

            int lastConfirmedEventPosition;
            if (page == 1) {
                lastConfirmedEventPosition = -1;
            } else {
                lastConfirmedEventPosition = loadLastSyncedPosition(SyncedPositionType.EVENT_RSVP,eventId);
            }
            int newItemCount = 0, deletedItemCount = 0;

            for (Rsvp retrievedRsvp : retrievedRsvps) {

                int foundPos;
                for (foundPos = lastConfirmedEventPosition+1; foundPos < currentEventRsvps.size(); foundPos++) {
                    EventRsvpReference checkEventRsvp = currentEventRsvps.get(foundPos);
                    if (retrievedRsvp.getId().equals(checkEventRsvp.getId())) {
                        break;
                    }
                }
                if (foundPos >= currentEventRsvps.size()) {
                    newItemCount++;
                    EventRsvpReference eventRsvpReference = new EventRsvpReference();
                    eventRsvpReference.setId(retrievedRsvp.getMember().getId());
                    eventRsvpReference.setNickname(retrievedRsvp.getMember().getNickname());
                    eventRsvpReference.setRsvpType(retrievedRsvp.getRsvpStatus() == Rsvp.RsvpStatus.YES ? EventRsvpReference.VALUE_RSVPTYPE_GOING : EventRsvpReference.VALUE_RSVPTYPE_MAYBE);
                    eventRsvpReference.setEventId(eventId);
                    eventRsvpReference.save();
                } else {
                    for (int i = lastConfirmedEventPosition+1; i < foundPos; i++) {
                        currentEventRsvps.get(i).delete();
                        deletedItemCount++;
                    }
                    lastConfirmedEventPosition = foundPos;
                }
                retrievedRsvp.getMember().mergeSave();
            }

            saveLastSyncedPosition(SyncedPositionType.EVENT_RSVP,eventId,lastConfirmedEventPosition+newItemCount);

            return retrievedRsvps.size() - deletedItemCount;
        } else {
            return Integer.MIN_VALUE;
        }
    }


    private int retrieveMemberGroups(String[] params) throws IOException {
        String memberId = getLocalId(params[0]);
        if (memberId == null) {
            return Integer.MIN_VALUE;
        }
        final int limit = getIntFromParams(params, 1, 10);
        final int page = getIntFromParams(params, 2, 1);

        final Call<List<GroupMembership>> getGroupMembershipsCall;
        getGroupMembershipsCall = getFetLifeApi().getMemberGroupMemberships(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), memberId, limit, page);

        Response<List<GroupMembership>> groupMembershipsResponse = getGroupMembershipsCall.execute();
        if (groupMembershipsResponse.isSuccessful()) {

            final List<GroupMembership> retrievedGroupMemberships = groupMembershipsResponse.body();
            List<GroupMembershipReference> currentGroups = new Select().from(GroupMembershipReference.class).where(GroupMembershipReference_Table.id.is(memberId)).orderBy(OrderBy.fromProperty(GroupMembershipReference_Table.lastVisitedAt).descending()).queryList();

            int lastConfirmedGroupPosition;
            if (page == 1) {
                lastConfirmedGroupPosition = -1;
            } else {
                lastConfirmedGroupPosition = loadLastSyncedPosition(SyncedPositionType.GROUP,memberId);
            }
            int newItemCount = 0, deletedItemCount = 0;

            for (GroupMembership retrievedGroupMembership : retrievedGroupMemberships) {
                if (retrievedGroupMembership.getGroup() == null) {
                    continue;
                }
                int foundPos;
                for (foundPos = lastConfirmedGroupPosition+1; foundPos < currentGroups.size(); foundPos++) {
                    GroupMembershipReference checkGroup = currentGroups.get(foundPos);
                    if (checkGroup!= null && retrievedGroupMembership.getId().equals(checkGroup.getId())) {
                        break;
                    }
                }
                if (foundPos >= currentGroups.size()) {
                    newItemCount++;
                    GroupMembershipReference groupMembershipReference = new GroupMembershipReference();
                    groupMembershipReference.setId(retrievedGroupMembership.getId());
                    groupMembershipReference.setCreatedAt(retrievedGroupMembership.getCreatedAt());
                    groupMembershipReference.setLastVisitedAt(retrievedGroupMembership.getLastVisitedAt());
                    groupMembershipReference.setMemberId(memberId);
                    groupMembershipReference.setGroupId(retrievedGroupMembership.getGroup().getId());
                    groupMembershipReference.save();
                } else {
                    for (int i = lastConfirmedGroupPosition+1; i < foundPos; i++) {
                        //temp workaround
                        //currentGroups.get(i).delete();
                        deletedItemCount++;
                    }
                    lastConfirmedGroupPosition = foundPos;
                }
                retrievedGroupMembership.getGroup().save();
            }

            saveLastSyncedPosition(SyncedPositionType.GROUP,memberId,lastConfirmedGroupPosition+newItemCount);

            return retrievedGroupMemberships.size() - deletedItemCount;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int retrieveMemberEvents(String[] params) throws IOException {
        String memberId = getLocalId(params[0]);
        if (memberId == null) {
            return Integer.MIN_VALUE;
        }
        final int limit = getIntFromParams(params, 1, 10);
        final int page = getIntFromParams(params, 2, 1);

        final Call<List<Rsvp>> getRsvpsCall;
        getRsvpsCall = getFetLifeApi().getMemberRsvps(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), memberId, /*"start_date_time",*/ limit, page);

        Response<List<Rsvp>> rsvpsResponse = getRsvpsCall.execute();
        if (rsvpsResponse.isSuccessful()) {

            final List<Rsvp> retrievedRsvps = rsvpsResponse.body();
            List<EventReference> currentEvents = new Select().from(EventReference.class).where(EventReference_Table.userId.is(memberId)).orderBy(OrderBy.fromProperty(EventReference_Table.date).descending()).queryList();

            int lastConfirmedEventPosition;
            if (page == 1) {
                lastConfirmedEventPosition = -1;
            } else {
                lastConfirmedEventPosition = loadLastSyncedPosition(SyncedPositionType.EVENT,memberId);
            }
            int newItemCount = 0, deletedItemCount = 0;

            for (Rsvp retrievedRsvp : retrievedRsvps) {
                Event retrievedEvent = retrievedRsvp.getEvent();

                int foundPos;
                for (foundPos = lastConfirmedEventPosition+1; foundPos < currentEvents.size(); foundPos++) {
                    EventReference checkEvent = currentEvents.get(foundPos);
                    if (retrievedEvent.getId().equals(checkEvent.getId())) {
                        checkEvent.setRsvpStatus(retrievedRsvp.getRsvpStatus());
                        checkEvent.save();
                        break;
                    }
                }
                if (foundPos >= currentEvents.size()) {
                    newItemCount++;
                    EventReference eventReference = new EventReference();
                    eventReference.setId(retrievedEvent.getId());
                    eventReference.setCreatedAt(retrievedEvent.getCreatedAt());
                    eventReference.setRsvpStatus(retrievedRsvp.getRsvpStatus());
                    eventReference.setUserId(memberId);
                    eventReference.save();
                } else {
                    for (int i = lastConfirmedEventPosition+1; i < foundPos; i++) {
                        currentEvents.get(i).delete();
                        deletedItemCount++;
                    }
                    lastConfirmedEventPosition = foundPos;
                }
                retrievedEvent.save();
            }

            saveLastSyncedPosition(SyncedPositionType.EVENT,memberId,lastConfirmedEventPosition+newItemCount);

            return retrievedRsvps.size() - deletedItemCount;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int retrieveMemberWritings(String[] params) throws IOException {
        String memberId = getLocalId(params[0]);
        if (memberId == null) {
            return Integer.MIN_VALUE;
        }
        final int limit = getIntFromParams(params, 1, 10);
        final int page = getIntFromParams(params, 2, 1);

        final Call<List<Writing>> getWritingsCall;
        getWritingsCall = getFetLifeApi().getMemberWritings(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), memberId, /*"start_date_time",*/ limit, page);

        Response<List<Writing>> writingsResponse = getWritingsCall.execute();
        if (writingsResponse.isSuccessful()) {

            final List<Writing> retrievedWritings = writingsResponse.body();
            List<WritingReference> currentWritings = new Select().from(WritingReference.class).where(WritingReference_Table.userId.is(memberId)).orderBy(OrderBy.fromProperty(WritingReference_Table.date).descending()).queryList();

            int lastConfirmedEventPosition;
            if (page == 1) {
                lastConfirmedEventPosition = -1;
            } else {
                lastConfirmedEventPosition = loadLastSyncedPosition(SyncedPositionType.WRITING,memberId);
            }
            int newItemCount = 0, deletedItemCount = 0;

            for (Writing retrievedWriting : retrievedWritings) {

                int foundPos;
                for (foundPos = lastConfirmedEventPosition+1; foundPos < currentWritings.size(); foundPos++) {
                    WritingReference checkEvent = currentWritings.get(foundPos);
                    if (retrievedWriting.getId().equals(checkEvent.getId())) {
                        break;
                    }
                }
                if (foundPos >= currentWritings.size()) {
                    newItemCount++;
                    WritingReference writingReference = new WritingReference();
                    writingReference.setId(retrievedWriting.getId());
                    writingReference.setCreatedAt(retrievedWriting.getCreatedAt());
                    writingReference.setDate(retrievedWriting.getDate());
                    writingReference.setUserId(memberId);
                    writingReference.save();
                } else {
                    for (int i = lastConfirmedEventPosition+1; i < foundPos; i++) {
                        currentWritings.get(i).delete();
                        deletedItemCount++;
                    }
                    lastConfirmedEventPosition = foundPos;
                }
                retrievedWriting.save();
            }

            saveLastSyncedPosition(SyncedPositionType.WRITING,memberId,lastConfirmedEventPosition+newItemCount);

            return retrievedWritings.size() - deletedItemCount;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int cancelFriendRequest(String[] params) throws IOException {
        String memberId = getLocalId(params[0]);
        if (memberId == null) {
            return Integer.MIN_VALUE;
        }

        int page = 1;

        while (true) {
            Call<List<FriendRequest>> getFriendRequestsCall = getFetLifeApi().getFriendRequests(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), PARAM_VALUE_FRIEND_REQUEST_SENT, MAX_PAGE_LIMIT, page++);
            Response<List<FriendRequest>> friendRequestsResponse = getFriendRequestsCall.execute();
            if (!friendRequestsResponse.isSuccessful()) {
                return Integer.MIN_VALUE;
            }
            final List<FriendRequest> friendRequests = friendRequestsResponse.body();
            if (friendRequests.isEmpty()) {
                return Integer.MIN_VALUE;
            }
            for (FriendRequest friendRequest : friendRequests) {
                if (memberId.equals(friendRequest.getTargetMemberId())) {
                    Call<FriendRequest> friendRequestsCall = getFetLifeApi().cancelFriendRequest(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), friendRequest.getId());
                    Response<FriendRequest> friendRequestResponse = friendRequestsCall.execute();
                    if (friendRequestResponse.isSuccessful()) {
                        return 1;
                    } else {
                        return Integer.MIN_VALUE;
                    }
                }
            }
            //Safety check to never go to endless loop not even if the API behaviour change.
            if (page == 20) {
                Crashlytics.logException(new Exception("Cancel friend request page limit has reached"));
                return Integer.MIN_VALUE;
            }
        }
    }

    private int cancelFriendship(String[] params) throws IOException {
        String memberId = getLocalId(params[0]);
        if (memberId == null) {
            return Integer.MIN_VALUE;
        }
        Call<ResponseBody> removeFriendshipCall = getFetLifeApi().removeFriendship(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), memberId);
        Response<ResponseBody> response = removeFriendshipCall.execute();
        return response.isSuccessful() ? 1 : Integer.MIN_VALUE;
    }

    private int retrieveFriendRequests(String... params) throws IOException {
        final int limit = getIntFromParams(params, 0, 10);
        final int page = getIntFromParams(params, 1, 1);

        Call<List<FriendRequest>> getFriendRequestsCall = getFetLifeApi().getFriendRequests(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), PARAM_VALUE_FRIEND_REQUEST_RECEIVED, limit, page);
        Response<List<FriendRequest>> friendRequestsResponse = getFriendRequestsCall.execute();
        if (friendRequestsResponse.isSuccessful()) {
            final List<FriendRequest> friendRequests = friendRequestsResponse.body();
            FlowManager.getDatabase(FetLifeDatabase.class).executeTransaction(new ITransaction() {
                @Override
                public void execute(DatabaseWrapper databaseWrapper) {
                    for (FriendRequest friendRequest : friendRequests) {
                        friendRequest.getMember().mergeSave();
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
            return friendRequests.size();
        } else {
            return Integer.MIN_VALUE;
        }
    }


    //****
    //Notification sending methods
    //****

    private void sendAuthenticationFailedNotification() {
        getFetLifeApplication().getEventBus().post(new AuthenticationFailedEvent());
    }

    private void sendLoadStartedNotification(String action, String[] params) {
        switch (action) {
            case ACTION_APICALL_LOGON_USER:
                getFetLifeApplication().getEventBus().post(new LoginStartedEvent());
                break;
            case ACTION_APICALL_UPLOAD_PICTURE:
                getFetLifeApplication().getEventBus().post(new PictureUploadStartedEvent(Integer.toString(callCount)));
                break;
            case ACTION_APICALL_UPLOAD_VIDEO:
            case ACTION_APICALL_UPLOAD_VIDEO_CHUNK:
                //Invoked directly from the methods
                break;
            default:
                getFetLifeApplication().getEventBus().post(new ServiceCallStartedEvent(action,params));
                break;
        }
    }

    private void sendLoadFinishedNotification(String action, int count, String... params) {
        switch (action) {
            case ACTION_APICALL_LOGON_USER:
                getFetLifeApplication().getEventBus().post(new LoginFinishedEvent());
                break;
            case ACTION_APICALL_UPLOAD_PICTURE:
                getFetLifeApplication().getEventBus().post(new PictureUploadFinishedEvent(Integer.toString(callCount)));
                break;
            default:
                getFetLifeApplication().getEventBus().post(new ServiceCallFinishedEvent(action, count, params));
                break;
        }
    }

    private void sendLoadFailedNotification(String action, String... params) {
        LogUtil.writeLog("sendLoadFailedNotification("+action+"):"+getFetLifeApplication().getFetLifeService().getLastResponseCode()+":"+getFetLifeApplication().getFetLifeService().getLastResponseBody());
        switch (action) {
            case ACTION_APICALL_LOGON_USER:
                getFetLifeApplication().getEventBus().post(new LoginFailedEvent());
                break;
            case ACTION_APICALL_UPLOAD_PICTURE:
                getFetLifeApplication().getEventBus().post(new PictureUploadFailedEvent(Integer.toString(callCount), false));
                break;
            default:
                getFetLifeApplication().getEventBus().post(new ServiceCallFailedEvent(action, false, params));
                break;
        }
    }

    private void sendConnectionFailedNotification(String action, String... params) {
        LogUtil.writeLog("sendConnectionFailedNotification("+action+"):"+getFetLifeApplication().getFetLifeService().getLastResponseCode()+":"+getFetLifeApplication().getFetLifeService().getLastResponseBody());
        switch (action) {
            case ACTION_APICALL_LOGON_USER:
                getFetLifeApplication().getEventBus().post(new LoginFailedEvent(true));
                break;
            default:
                getFetLifeApplication().getEventBus().post(new ServiceCallFailedEvent(action, true, params));
                break;
        }
    }


    //****
    //Helper access methods to retrieve references from other holders
    //****

    private FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplication();
    }

    private FetLifeApi getFetLifeApi() {
        return getFetLifeApplication().getFetLifeService().getFetLifeApi();
    }

    private FetLifeMultipartUploadApi getFetLifeUploadApi() {
        return getFetLifeApplication().getFetLifeService().getFetLifeMultipartUploadApi();
    }


    //****
    //Helper utility methods
    //****

    public static int getIntFromParams(String[] params, int pageParamPosition, int defaultValue) {
        int param = defaultValue;
        if (params != null && params.length > pageParamPosition) {
            try {
                param = Integer.parseInt(params[pageParamPosition]);
            } catch (NumberFormatException nfe) {
            }
        }
        return param;
    }

    public static boolean getBoolFromParams(String[] params, int pageParamPosition, boolean defaultValue) {
        boolean param = defaultValue;
        if (params != null && params.length > pageParamPosition) {
            try {
                param = Boolean.parseBoolean(params[pageParamPosition]);
            } catch (NumberFormatException nfe) {
            }
        }
        return param;
    }

    private enum SyncedPositionType {
        PICTURE,
        STATUS,
        EVENT,
        EVENT_RSVP,
        FRIEND,
        CONVERSATION, FOLLOWER, FOLLOWING, VIDEO, GROUP, GROUP_MEMBERS, GROUP_DISCUSSIONS, WRITING;

        @Override
        public String toString() {
            return getClass().getSimpleName() + "." + super.toString();
        }
    }

    private void saveLastSyncedPosition(SyncedPositionType syncedPositionType, String modifier, int position) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getFetLifeApplication());
        preferences.edit().putInt(syncedPositionType.toString() + modifier, position).apply();
    }

    private int loadLastSyncedPosition(SyncedPositionType syncedPositionType, String modifier) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getFetLifeApplication());
        return preferences.getInt(syncedPositionType.toString() + modifier, -1);
    }

    private String getClientSecret() {

        try {
            String iv = BuildConfig.SECURE_API_IV;
            if (iv.isEmpty()) {
                return BuildConfig.CLIENT_SECRET;
            }

            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = pInfo.signatures[0].toByteArray();
            InputStream input = new ByteArrayInputStream(cert);
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            X509Certificate c = (X509Certificate) cf.generateCertificate(input);

            byte[] key = new byte[16];
            System.arraycopy(c.getPublicKey().getEncoded(),0,key,0,16);

            SecretKey secret = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(Base64.decode(iv,Base64.NO_WRAP)));

            input.close();

            return new String(cipher.doFinal(Base64.decode(BuildConfig.CLIENT_SECRET,Base64.NO_WRAP)));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getLocalId(final String serverId) throws IOException {
        String localId = serverId;
        if (ServerIdUtil.isServerId(serverId)) {
            localId = ServerIdUtil.getLocalId(serverId);
            if (localId == null) {
                Call<AppId> getAppIdCall = getFetLifeApi().getAppId(FetLifeService.AUTH_HEADER_PREFIX + getAccessToken(), ServerIdUtil.removePrefix(serverId));
                Response<AppId> getAppIdResponse = getAppIdCall.execute();
                if (getAppIdResponse.isSuccessful()) {
                    localId = getAppIdResponse.body().getId();
                    ServerIdUtil.setLocalId(serverId,localId);
                } else {
                    return null;
                }
            }
        }
        return localId;
    }


}
