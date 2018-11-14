package com.bitlove.fetlife.notification;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.os.Build;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.view.screen.resource.ConversationsActivity;
import com.bitlove.fetlife.view.screen.resource.MessagesActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
public class StackedNotification extends OneSignalNotification {

    protected List<OneSignalNotification> subNotificaions = new ArrayList<>();

    public StackedNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group, List<OneSignalNotification> subNotificaions) {
        super(id, title, message, launchUrl, group != null ? group : "", additionalData);
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
//        switch (group) {
//            case NotificationParser.JSON_VALUE_GROUP_LEGACY_FETLIFE:
//            case NotificationParser.JSON_VALUE_GROUP_INFO:
//                NotificationHistoryActivity.startActivity(fetLifeApplication, true);
//                break;
//            case NotificationParser.JSON_VALUE_GROUP_LEGACY_MESSAGE:
//            case NotificationParser.JSON_VALUE_GROUP_FETLIFE_MESSAGE:
//                MessageNotification sampleMessageNotification = isSameConversations(subNotificaions);
//                if (sampleMessageNotification != null) {
//                    startMessageActivity(fetLifeApplication, sampleMessageNotification);
//                } else {
//                    ConversationsActivity.startActivity(fetLifeApplication, true);
//                }
//                break;
//            case "":
//                //Issue opened for OneSignal because of the missing grouping information
//                missingGroupFallBack(fetLifeApplication);
//                break;
//            default:
//                NotificationHistoryActivity.startActivity(fetLifeApplication, true);
//        }
    }

    @Override
    public String getAssociatedPreferenceKey(Context context) {
        return null;
    }

    private void missingGroupFallBack(FetLifeApplication fetLifeApplication) {
//        MessageNotification sampleMessageNotification = isSameConversations(getSubNotificaions());
//        if (sampleMessageNotification != null) {
//            startMessageActivity(fetLifeApplication, sampleMessageNotification);
//        } else {
//            OneSignalNotification firstNotification = subNotificaions.isEmpty() ? null : subNotificaions.get(0);
//            if (firstNotification != null && firstNotification instanceof MessageNotification) {
//                ConversationsActivity.startActivity(fetLifeApplication, true);
//            } else {
//                NotificationHistoryActivity.startActivity(fetLifeApplication, true);
//            }
//        }
    }

    private void startMessageActivity(FetLifeApplication fetLifeApplication, MessageNotification sampleMessageNotification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TaskStackBuilder.create(fetLifeApplication).addNextIntent(ConversationsActivity.createIntent(fetLifeApplication, true)).addNextIntent(MessagesActivity.createIntent(fetLifeApplication, sampleMessageNotification.conversationId, sampleMessageNotification.nickname, null, true)).startActivities();
        } else {
            ConversationsActivity.startActivity(fetLifeApplication, true);
            MessagesActivity.startActivity(fetLifeApplication, sampleMessageNotification.conversationId, sampleMessageNotification.nickname, null, true);
        }
    }

}
