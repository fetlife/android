package com.bitlove.fetlife.notification;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.os.Build;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.view.activity.resource.ConversationsActivity;
import com.bitlove.fetlife.view.activity.resource.MessagesActivity;
import com.bitlove.fetlife.view.activity.resource.NotificationHistoryActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StackedNotification extends OneSignalNotification {

    protected List<OneSignalNotification> subNotificaions = new ArrayList<>();

    public StackedNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group, List<OneSignalNotification> subNotificaions) {
        super(title, message, launchUrl, additionalData, id, group != null ? group : "");
        this.subNotificaions = subNotificaions;
    }

    public List<OneSignalNotification> getSubNotificaions() {
        return Collections.unmodifiableList(subNotificaions);
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        //Should not be invoked
        return false;
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
        switch (group) {
            case NotificationParser.JSON_VALUE_GROUP_INFO:
                NotificationHistoryActivity.startActivity(fetLifeApplication, true);
                break;
            case NotificationParser.JSON_VALUE_GROUP_FETLIFE:
            case NotificationParser.JSON_VALUE_GROUP_MESSAGE:
                MessageNotification sampleMessageNotification = isSameConversations(subNotificaions);
                if (sampleMessageNotification != null) {
                    startMessageActivity(fetLifeApplication, sampleMessageNotification);
                } else {
                    ConversationsActivity.startActivity(fetLifeApplication);
                }
                break;
            case "":
                //Issue opened for OneSignal because of the missing grouping information
                missingGroupFallBack(fetLifeApplication);
                break;
            default:
                NotificationHistoryActivity.startActivity(fetLifeApplication, true);
        }
    }

    @Override
    public String getAssociatedPreferenceKey(Context context) {
        return null;
    }

    private void missingGroupFallBack(FetLifeApplication fetLifeApplication) {
        MessageNotification sampleMessageNotification = isSameConversations(getSubNotificaions());
        if (sampleMessageNotification != null) {
            startMessageActivity(fetLifeApplication, sampleMessageNotification);
        } else {
            OneSignalNotification firstNotification = subNotificaions.isEmpty() ? null : subNotificaions.get(0);
            if (firstNotification != null && firstNotification instanceof MessageNotification) {
                ConversationsActivity.startActivity(fetLifeApplication);
            } else {
                NotificationHistoryActivity.startActivity(fetLifeApplication, true);
            }
        }
    }

    private void startMessageActivity(FetLifeApplication fetLifeApplication, MessageNotification sampleMessageNotification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder.create(fetLifeApplication).addNextIntent(ConversationsActivity.createIntent(fetLifeApplication)).addNextIntent(MessagesActivity.createIntent(fetLifeApplication, sampleMessageNotification.conversationId, sampleMessageNotification.nickname, true)).startActivities();
        } else {
            ConversationsActivity.startActivity(fetLifeApplication);
            MessagesActivity.startActivity(fetLifeApplication, sampleMessageNotification.conversationId, sampleMessageNotification.nickname, true);
        }
    }

    private MessageNotification isSameConversations(List<OneSignalNotification> notifications) {
        MessageNotification lastMatchingMessageNotification = null;
        for (OneSignalNotification notification : notifications) {
            if (!(notification instanceof MessageNotification)) {
                return null;
            }
            if (lastMatchingMessageNotification == null) {
                lastMatchingMessageNotification = (MessageNotification) notification;
                continue;
            }
            String conversationId = ((MessageNotification) notification).conversationId;
            if (!lastMatchingMessageNotification.conversationId.equals(conversationId)) {
                return null;
            }
        }
        return lastMatchingMessageNotification;
    }
}
