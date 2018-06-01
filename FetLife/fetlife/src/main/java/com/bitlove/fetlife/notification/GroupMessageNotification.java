package com.bitlove.fetlife.notification;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.NewGroupMessageEvent;
import com.bitlove.fetlife.model.pojos.fetlife.db.NotificationHistoryItem;
import com.bitlove.fetlife.model.pojos.fetlife.db.NotificationHistoryItem_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.StringUtil;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.ConversationsActivity;
import com.bitlove.fetlife.view.screen.resource.groups.GroupActivity;
import com.bitlove.fetlife.view.screen.resource.groups.GroupMessagesActivity;
import com.bitlove.fetlife.view.screen.resource.groups.GroupsActivity;
import com.crashlytics.android.Crashlytics;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.InvalidDBConfiguration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GroupMessageNotification extends OneSignalNotification {

    private static List<GroupMessageNotification> notifications = new ArrayList<GroupMessageNotification>();

    protected String groupId;
    protected String groupDiscussionId;
    protected String groupDiscussionTitle;

    public GroupMessageNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message,launchUrl,additionalData,id, group);
        JSONObject apiContainer = additionalData.optJSONObject(NotificationParser.JSON_FIELD_OBJECT_API);
        if (apiContainer == null) {
            Crashlytics.log("Missing Group Notification API key");
            Crashlytics.log("title: " + title);
            Crashlytics.log("message: " + message);
            Crashlytics.log("additional data: " + additionalData != null ? additionalData.toString() : "null");
            Crashlytics.logException(new Exception());
        } else {
            groupId = apiContainer.optString(NotificationParser.JSON_FIELD_STRING_GROUPID);
            groupDiscussionId = apiContainer.optString(NotificationParser.JSON_FIELD_STRING_GROUPPOSTID);
            GroupPost groupPost = GroupPost.loadGroupPost(groupDiscussionId);
            if (groupPost != null) {
                groupDiscussionTitle = groupPost.getTitle();
            } else {
                groupDiscussionTitle = apiContainer.optString(NotificationParser.JSON_FIELD_STRING_GROUP_POST_TITLE);
                if (TextUtils.isEmpty(groupDiscussionTitle)) {
                    groupDiscussionTitle = apiContainer.optString(NotificationParser.JSON_FIELD_STRING_GROUP_NAME);
                }
            }
        }

        NotificationHistoryItem notificationHistoryItem = createNotificationItem(NOTIFICATION_ID_GROUP, groupDiscussionId);
        if (groupDiscussionId != null) {
            notificationHistoryItem.setLaunchUrl(getInnerLaunchUrl());
            NotificationHistoryItem toBeCollapsedNotification;
            try {
                toBeCollapsedNotification = new Select().from(NotificationHistoryItem.class).where(NotificationHistoryItem_Table.collapseId.eq(notificationHistoryItem.getCollapseId())).querySingle();
            } catch (InvalidDBConfiguration | IllegalStateException idbe) {
                toBeCollapsedNotification = null;
            }
            if (toBeCollapsedNotification != null) {
                OneSignal.cancelNotification(toBeCollapsedNotification.getDisplayId());
                toBeCollapsedNotification.delete();
            }
        }
        try {
            notificationHistoryItem.save();
        } catch (Throwable t) {
            Crashlytics.logException(t);
        }
    }

    private String getInnerLaunchUrl() {
        return LAUNCH_URL_PREFIX + getClass().getName()+LAUNCH_URL_PARAM_SEPARATOR+groupId+LAUNCH_URL_PARAM_SEPARATOR+groupDiscussionId+LAUNCH_URL_PARAM_SEPARATOR+groupDiscussionTitle;
    }

    public static void handleInnerLaunchUrl(Context context,String launchUrl) {
        String[] params = launchUrl.substring(LAUNCH_URL_PREFIX.length()).split(LAUNCH_URL_PARAM_SEPARATOR);
        GroupMessagesActivity.startActivity(context, params[1], params[2], params[3], null, true);
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(groupDiscussionId)) {
            return false;
        }

        FetLifeApiIntentService.startApiCall(fetLifeApplication, FetLifeApiIntentService.ACTION_APICALL_GROUP, groupId);
        FetLifeApiIntentService.startApiCall(fetLifeApplication, FetLifeApiIntentService.ACTION_APICALL_GROUP_MESSAGES, groupId, groupDiscussionId);

        boolean groupDiscussionInForeground = false;
        boolean appInForeground = fetLifeApplication.isAppInForeground();

        if (appInForeground) {
            fetLifeApplication.getEventBus().post(new NewGroupMessageEvent(groupId,groupDiscussionId));
            Activity foregroundActivity = fetLifeApplication.getForegroundActivity();
            if (foregroundActivity instanceof GroupMessagesActivity) {
                return groupId.equals(((GroupMessagesActivity)foregroundActivity).getGroupId()) && groupDiscussionId.equals(((GroupMessagesActivity)foregroundActivity).getGroupDiscussionId());
            }
        }

        //TODO: display in app notification if the user is not on the same message screen
        return groupDiscussionInForeground;
    }
    public static void clearNotifications() {
        synchronized (notifications) {
            notifications.clear();
        }
    }

    @Override
    public void display(FetLifeApplication fetLifeApplication) {
        synchronized (notifications) {
            notifications.add(this);

            NotificationCompat.Builder notificationBuilder = getDefaultNotificationBuilder(fetLifeApplication);

            List<String> messages = getGroupedMessageTexts(fetLifeApplication, notifications);
            String title = notifications.size() == 1 ? fetLifeApplication.getString(R.string.noification_title_new_group_message) : fetLifeApplication.getString(R.string.noification_title_new_group_messages,notifications.size());
            String firstMessage = messages.get(0);

            notificationBuilder.setContentTitle(title).setContentText(firstMessage);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(title);
            //TODO: localization
            inboxStyle.setSummaryText("…");
            for (String message : messages) {
                inboxStyle.addLine(message);
            }
            notificationBuilder.setStyle(inboxStyle);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(fetLifeApplication);
            notificationManager.notify(OneSignalNotification.NOTIFICATION_ID_GROUP, notificationBuilder.build());

            onNotificationDisplayed(fetLifeApplication,NOTIFICATION_ID_DO_NOT_COLLAPSE);
        }
    }

    @Override
    PendingIntent getPendingIntent(final Context context) {
        synchronized (notifications) {
            if (!TextUtils.isEmpty(groupId) && !TextUtils.isEmpty(groupDiscussionId) && getGroupedMessageTexts(FetLifeApplication.getInstance(),notifications).size() == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                //TODO: Change group title
                Intent contentIntent = GroupMessagesActivity.createIntent(context, groupId, groupDiscussionId, groupDiscussionTitle, null, true);
                contentIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE,getNotificationType());
                return TaskStackBuilder.create(context).addNextIntent(GroupsActivity.createIntent(context,true)).addNextIntent(GroupActivity.createIntent(context, groupId, null, true)).addNextIntent(contentIntent).getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                Intent contentIntent = GroupsActivity.createIntent(context, true);
                contentIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE,getNotificationType());
                return PendingIntent.getActivity(context,0,contentIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }
    }

    @Override
    @Deprecated
    public void onClick(FetLifeApplication fetLifeApplication) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            try {
//                TaskStackBuilder.create(fetLifeApplication).addNextIntent(ConversationsActivity.createIntent(fetLifeApplication, true)).addNextIntent(MessagesActivity.createIntent(fetLifeApplication, conversationId, nickname, null, true)).startActivities();
//            } catch (NullPointerException npe) {
//                //Apply workaround for OS bug
//                startLegacyConversationAndMessageActivity(fetLifeApplication);
//            }
//        } else {
//            startLegacyConversationAndMessageActivity(fetLifeApplication);
//        }
    }

//    private void startLegacyConversationAndMessageActivity(FetLifeApplication fetLifeApplication) {
//        ConversationsActivity.startActivity(fetLifeApplication, true);
//        MessagesActivity.startActivity(fetLifeApplication, conversationId, nickname, null, true);
//    }

    @Override
    public String getAssociatedPreferenceKey(Context context) {
        return context.getString(R.string.settings_key_notification_group_messages_enabled);
    }

    private List<String> getGroupedMessageTexts(FetLifeApplication fetLifeApplication, List<GroupMessageNotification> notifications) {
        LinkedHashMap<String,Integer> userMessageGroups = new LinkedHashMap<>();
        Map<String,String> groupDiscussionTitles = new HashMap<>();
        for (GroupMessageNotification notification : notifications) {
            Integer userMessageCount = userMessageGroups.get(notification.groupDiscussionId);
            if (userMessageCount == null) {
                userMessageCount = 1;
            } else {
                userMessageCount++;
            }
            groupDiscussionTitles.put(notification.groupDiscussionId,notification.groupDiscussionTitle);
            userMessageGroups.put(notification.groupDiscussionId,userMessageCount);
        }
        List<String> messages = new ArrayList<>();
        for (Map.Entry<String,Integer> userMessageGroup : userMessageGroups.entrySet()) {
            messages.add(new Integer(1).equals(userMessageGroup.getValue()) ? fetLifeApplication.getString(R.string.noification_text_new_group_message,groupDiscussionTitles.get(userMessageGroup.getKey())) : fetLifeApplication.getString(R.string.noification_text_new_group_messages,userMessageGroup.getValue(),groupDiscussionTitles.get(userMessageGroup.getKey())));
        }
        Collections.reverse(messages);
        return messages;
    }
}
