package com.bitlove.fetlife.notification;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.NewMessageEvent;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.ConversationsActivity;
import com.bitlove.fetlife.view.screen.resource.MessagesActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MessageNotification extends OneSignalNotification {

    private static List<MessageNotification> notifications = new ArrayList<MessageNotification>();

    protected String conversationId;
    protected String nickname;

    public MessageNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(id, title, message,launchUrl, group, additionalData);
        conversationId = additionalData.optString(NotificationParser.JSON_FIELD_STRING_CONVERSATIONID);
        nickname = additionalData.optString(NotificationParser.JSON_FIELD_STRING_NICKNAME);
        notificationType = NotificationParser.JSON_VALUE_TYPE_CONVERSATION_RESPONSE;
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        if (conversationId != null) {
            FetLifeApiIntentService.startApiCall(fetLifeApplication, FetLifeApiIntentService.ACTION_APICALL_MESSAGES, conversationId);
        } else {
            throw new IllegalArgumentException("Missing field");
        }

        boolean conversationInForeground = false;
        boolean appInForeground = fetLifeApplication.isAppInForeground();

        if (appInForeground) {
            fetLifeApplication.getEventBus().post(new NewMessageEvent(conversationId));
            Activity foregroundActivity = fetLifeApplication.getForegroundActivity();
            if (foregroundActivity instanceof MessagesActivity) {
                conversationInForeground = conversationId.equals(((MessagesActivity)foregroundActivity).getConversationId());
            } else if (foregroundActivity instanceof  ConversationsActivity) {
                conversationInForeground = true;
            }
        }

        //TODO: display in app notification if the user is not on the same message screen
        return conversationInForeground;
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
            String title = notifications.size() == 1 ? fetLifeApplication.getString(R.string.noification_title_new_message) : fetLifeApplication.getString(R.string.noification_title_new_messages,notifications.size());
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
            notificationManager.notify(OneSignalNotification.NOTIFICATION_ID_MESSAGE, notificationBuilder.build());
        }
    }

    @Override
    PendingIntent getPendingIntent(Context context) {
        synchronized (notifications) {
            if (getGroupedMessageTexts(FetLifeApplication.getInstance(),notifications).size() == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Intent contentIntent = MessagesActivity.createIntent(context, conversationId, nickname, null, true);
                contentIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE,getNotificationType());
                return TaskStackBuilder.create(context).addNextIntent(ConversationsActivity.createIntent(context, true)).addNextIntent(contentIntent).getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }

        Intent contentIntent = ConversationsActivity.createIntent(context,true);
        PendingIntent contentPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        contentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        return contentPendingIntent;
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
        return context.getString(R.string.settings_key_notification_messages_enabled);
    }

    private List<String> getGroupedMessageTexts(FetLifeApplication fetLifeApplication, List<MessageNotification> notifications) {
        LinkedHashMap<String,Integer> userMessageGroups = new LinkedHashMap<>();
        for (MessageNotification notification : notifications) {
            Integer userMessageCount = userMessageGroups.get(notification.title);
            if (userMessageCount == null) {
                userMessageCount = 1;
            } else {
                userMessageCount++;
            }
            userMessageGroups.put(notification.title,userMessageCount);
        }
        List<String> messages = new ArrayList<>();
        for (Map.Entry<String,Integer> userMessageGroup : userMessageGroups.entrySet()) {
            messages.add(new Integer(1).equals(userMessageGroup.getValue()) ? fetLifeApplication.getString(R.string.noification_text_new_message,userMessageGroup.getKey()) : fetLifeApplication.getString(R.string.noification_text_new_messages,userMessageGroup.getValue(),userMessageGroup.getKey()));
        }
        Collections.reverse(messages);
        return messages;
    }
}
