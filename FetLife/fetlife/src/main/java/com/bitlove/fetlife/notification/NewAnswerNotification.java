package com.bitlove.fetlife.notification;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.db.NotificationHistoryItem;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.TurboLinksViewActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NewAnswerNotification extends OneSignalNotification {

    private static List<NewAnswerNotification> notifications = new ArrayList<>();

    public NewAnswerNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message, launchUrl, additionalData, id, group);
        notificationType = NotificationParser.JSON_VALUE_TYPE_QUESTIONS_NEW_ANSWER;
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

            List<String> newAnswers = getGroupedAnswerTexts(fetLifeApplication, notifications);
            String title = notifications.size() == 1 ? fetLifeApplication.getString(R.string.noification_title_new_answer) : fetLifeApplication.getString(R.string.noification_title_new_answers,notifications.size());
            String firstAnswer = newAnswers.get(0);

            notificationBuilder.setContentTitle(title).setContentText(firstAnswer);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(title);
            //TODO: localization
            inboxStyle.setSummaryText("…");
            for (String message : newAnswers) {
                inboxStyle.addLine(message);
            }
            notificationBuilder.setStyle(inboxStyle);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(fetLifeApplication);
            notificationManager.notify(OneSignalNotification.NOTIFICATION_ID_ANSWERS, notificationBuilder.build());

            onNotificationDisplayed(fetLifeApplication,NOTIFICATION_ID_DO_NOT_COLLAPSE);
        }
    }

    private List<String> getGroupedAnswerTexts(FetLifeApplication fetLifeApplication, List<NewAnswerNotification> notifications) {
        LinkedHashMap<String,Integer> newAnswerGroups = new LinkedHashMap<>();
        for (NewAnswerNotification notification : notifications) {
            Integer newAnswerCount = newAnswerGroups.get(notification.launchUrl);
            if (newAnswerCount == null) {
                newAnswerCount = 1;
            } else {
                newAnswerCount++;
            }
            newAnswerGroups.put(notification.launchUrl,newAnswerCount);
        }
        List<String> answers = new ArrayList<>();
        for (Map.Entry<String,Integer> newAnswerGroup : newAnswerGroups.entrySet()) {
            answers.add(new Integer(1).equals(newAnswerGroup.getValue()) ? fetLifeApplication.getString(R.string.noification_text_new_answer) : fetLifeApplication.getString(R.string.noification_text_new_answers,newAnswerGroup.getValue()));
        }
        Collections.reverse(answers);
        return answers;
    }


    @Override
    public void onNotificationDisplayed(FetLifeApplication fetLifeApplication, int notificationId) {
        NotificationHistoryItem notificationHistoryItem = createNotificationItem(notificationId, null);
        notificationHistoryItem.save();
    }

    @Override
    PendingIntent getPendingIntent(Context context) {
        Intent baseIntent = TurboLinksViewActivity.createIntent(context,"q",context.getString(R.string.title_activity_questions), true, TurboLinksViewActivity.FAB_LINK_NEW_QUESTION,true);
        baseIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE,getNotificationType());
        Intent contentIntent = TurboLinksViewActivity.createIntent(context,launchUrl.replaceAll("//fetlife.com","//app.fetlife.com"),null, false, null,false);
        return TaskStackBuilder.create(context).addNextIntent(baseIntent).addNextIntent(contentIntent).getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        return false;
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {

    }

    @Override
    public String getAssociatedPreferenceKey(Context context) {
        return context.getString(R.string.settings_key_notification_new_answers_enabled);
    }
}
