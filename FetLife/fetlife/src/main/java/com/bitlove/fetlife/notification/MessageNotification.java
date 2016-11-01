package com.bitlove.fetlife.notification;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.NewMessageEvent;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.activity.ConversationsActivity;
import com.bitlove.fetlife.view.activity.MessagesActivity;

import org.json.JSONObject;

public class MessageNotification extends OneSignalNotification {

    protected String conversationId;
    protected String nickname;

    public MessageNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message,launchUrl,additionalData,id, group);
        conversationId = additionalData.optString(NotificationParser.JSON_FIELD_STRING_CONVERSATIONID);
        nickname = additionalData.optString(NotificationParser.JSON_FIELD_STRING_NICKNAME);
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

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                TaskStackBuilder.create(fetLifeApplication).addNextIntent(ConversationsActivity.createIntent(fetLifeApplication)).addNextIntent(MessagesActivity.createIntent(fetLifeApplication, conversationId, nickname, true)).startActivities();
            } catch (NullPointerException npe) {
                //Apply workaround for OS bug
                startLegacyConversationAndMessageActivity(fetLifeApplication);
            }
        } else {
            startLegacyConversationAndMessageActivity(fetLifeApplication);
        }
    }

    private void startLegacyConversationAndMessageActivity(FetLifeApplication fetLifeApplication) {
        ConversationsActivity.startActivity(fetLifeApplication);
        MessagesActivity.startActivity(fetLifeApplication, conversationId, nickname, true);
    }

    @Override
    public String getAssociatedPreferenceKey(Context context) {
        return context.getString(R.string.settings_key_notification_messages_enabled);
    }
}
