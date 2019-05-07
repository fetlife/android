package com.bitlove.fetlife.legacy.inbound.onesignal.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bitlove.fetlife.R
import com.bitlove.fetlife.common.view.BaseActivity
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity
import org.json.JSONObject

class InfoNotification(notificationType: String, notificationIdRange: Int, title: String, message: String, launchUrl: String, mergeId: String?, collapseId: String?, additionalData: JSONObject, preferenceKey: String?) : OneSignalNotification(notificationType, notificationIdRange, title, message, launchUrl, mergeId, collapseId, additionalData, preferenceKey) {

    override fun getNotificationChannelName(context: Context): String? {
        return context.getString(R.string.settings_title_notification_info_enabled)
    }

    override fun getNotificationChannelDescription(context: Context): String? {
        return context.getString(R.string.settings_summary_notification_info_enabled)
    }

    override fun getSummaryTitle(notificationCount: Int, context: Context): String? {
        return context.resources.getQuantityString(R.plurals.noification_summary_title_info, notificationCount, notificationCount)
    }

    override fun getSummaryText(notificationCount: Int, context: Context): String? {
        return context.getString(R.string.noification_summary_text_info)
    }

    override fun getNotificationIntent(oneSignalNotification: OneSignalNotification, context: Context, order: Int): PendingIntent? {
        val contentIntent = if (oneSignalNotification.launchUrl != null) {
            Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_RECEIVER_FOREGROUND)
                data = Uri.parse(launchUrl)
            }
        } else {
            FetLifeWebViewActivity.createIntent(context, "notifications", true, true, R.id.navigation_bottom_notifications, true, null).apply {
                addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE, oneSignalNotification.notificationType)
                putExtra(BaseActivity.EXTRA_NOTIFICATION_MERGE_ID, oneSignalNotification.mergeId)
            }
        }
        return PendingIntent.getActivity(context,order,contentIntent,PendingIntent.FLAG_CANCEL_CURRENT)
    }
}