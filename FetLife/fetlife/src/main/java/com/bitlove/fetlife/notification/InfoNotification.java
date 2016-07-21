package com.bitlove.fetlife.notification;

import android.content.Intent;
import android.net.Uri;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.model.pojos.NotificationHistoryItem;
import com.bitlove.fetlife.model.pojos.NotificationHistoryItem_Table;
import com.bitlove.fetlife.view.NotificationHistoryActivity;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.json.JSONObject;

public class InfoNotification extends OneSignalNotification {

    private final String collapseId;

    public InfoNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message,launchUrl,additionalData,id,group);
        collapseId = additionalData.optString(NotificationParser.JSON_FIELD_STRING_COLLAPSE_ID);
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        return false;
    }

    @Override
    public void onNotificationDisplayed(FetLifeApplication fetLifeApplication, int notificationId) {
        NotificationHistoryItem notificationHistoryItem = createNotificationItem(notificationId);
        if (collapseId != null && collapseId.trim().length() != 0) {
            NotificationHistoryItem toBeCollapsedNotification = new Select().from(NotificationHistoryItem.class).where(NotificationHistoryItem_Table.collapseId.eq(notificationHistoryItem.getCollapseId())).querySingle();
            if (toBeCollapsedNotification != null) {
                OneSignal.cancelNotification(toBeCollapsedNotification.getDisplayId());
                toBeCollapsedNotification.delete();
            }
        }
        notificationHistoryItem.save();
    }

    protected NotificationHistoryItem createNotificationItem(int notificationId) {
        NotificationHistoryItem notificationItem = new NotificationHistoryItem();
        notificationItem.setDisplayId(notificationId);
        notificationItem.setDisplayHeader(title);
        notificationItem.setDisplayMessage(message);
        notificationItem.setLaunchUrl(launchUrl);
        notificationItem.setCollapseId(collapseId);
        return notificationItem;
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
        if (launchUrl != null && launchUrl.trim().length() != 0) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(launchUrl));
            fetLifeApplication.startActivity(intent);
        } else {
            NotificationHistoryActivity.startActivity(fetLifeApplication);
        }
    }
}
