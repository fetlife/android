package com.bitlove.fetlife.notification;

import android.app.TaskStackBuilder;
import android.os.Build;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.event.NewMessageEvent;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.ConversationsActivity;
import com.bitlove.fetlife.view.MessagesActivity;

import org.json.JSONObject;

public class MessageNotification extends OneSignalNotification {

    private String conversationId;
    private String nickname;

    public MessageNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message,launchUrl,additionalData,id, group);
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {

        conversationId = additionalData.optString(NotificationParser.JSON_FIELD_STRING_CONVERSATIONID);
        nickname = additionalData.optString(NotificationParser.JSON_FIELD_STRING_NICKNAME);
        if (conversationId != null) {
            FetLifeApiIntentService.startApiCall(fetLifeApplication, FetLifeApiIntentService.ACTION_APICALL_MESSAGES, conversationId);
        } else {
            throw new IllegalArgumentException("Missing field");
        }

        boolean appInForeground = fetLifeApplication.isAppInForeground();

        if (appInForeground) {
            fetLifeApplication.getEventBus().post(new NewMessageEvent(conversationId));
        }

        //TODO: display in app notification if the user is not on the same message screen
        return fetLifeApplication.isAppInForeground();
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder.create(fetLifeApplication).addNextIntent(ConversationsActivity.createIntent(fetLifeApplication)).addNextIntent(MessagesActivity.createIntent(fetLifeApplication, conversationId, nickname, true)).startActivities();
        } else {
            ConversationsActivity.startActivity(fetLifeApplication);
            MessagesActivity.startActivity(fetLifeApplication, conversationId, nickname, true);
        }
    }
}
