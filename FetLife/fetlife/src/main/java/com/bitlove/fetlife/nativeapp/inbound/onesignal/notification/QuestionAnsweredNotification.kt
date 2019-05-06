package com.bitlove.fetlife.nativeapp.inbound.onesignal.notification

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import com.bitlove.fetlife.R
import com.bitlove.fetlife.nativeapp.view.screen.BaseActivity
import com.bitlove.fetlife.webapp.navigation.WebAppNavigation
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity
import org.json.JSONObject

class QuestionAnsweredNotification(notificationType: String, notificationIdRange: Int, title: String, message: String, launchUrl: String, mergeId: String?, collapseId: String?, additionalData: JSONObject, preferenceKey: String?) : OneSignalNotification(notificationType, notificationIdRange, title, message, launchUrl, mergeId, collapseId, additionalData, preferenceKey) {

    override fun getNotificationChannelName(context: Context): String? {
        return context.getString(R.string.settings_title_notification_questions_enabled)
    }

    override fun getNotificationChannelDescription(context: Context): String? {
        return context.getString(R.string.settings_summary_notification_questions_enabled)
    }

    override fun getSummaryTitle(notificationCount: Int, context: Context): String? {
        return context.resources.getQuantityString(R.plurals.noification_summary_title_questions_new_answer, notificationCount, notificationCount)
    }

    override fun getSummaryText(notificationCount: Int, context: Context): String? {
        return context.getString(R.string.noification_summary_text_questions_new_answer)
    }

    override fun getNotificationTitle(oneSignalNotification: OneSignalNotification, notificationCount: Int, context: Context): String? {
        return context.resources.getQuantityString(R.plurals.noification_title_questions_new_answer, notificationCount, notificationCount)
    }

    override fun getNotificationText(oneSignalNotification: OneSignalNotification, notificationCount: Int, context: Context): String? {
        return context.getString(R.string.noification_text_questions_new_answer)
    }

    override fun getNotificationIntent(oneSignalNotification: OneSignalNotification, context: Context, order: Int): PendingIntent? {
        val baseIntent = FetLifeWebViewActivity.createIntent(context, "q", true, true, null, true, null)
        val contentIntent = FetLifeWebViewActivity.createIntent(context, oneSignalNotification.launchUrl?.replace("https://fetlife.com".toRegex(), WebAppNavigation.WEBAPP_BASE_URL) ?: WebAppNavigation.WEBAPP_BASE_URL, true, false, null, false, null)
        contentIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE, oneSignalNotification.notificationType)
        contentIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_MERGE_ID, oneSignalNotification.mergeId)
        //return PendingIntent.gentActivity(context, order, contentIntent, PendingIntent.FLAG_IMMUTABLE)
        return TaskStackBuilder.create(context).addNextIntentWithParentStack(baseIntent).addNextIntent(contentIntent).getPendingIntent(order, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    override fun getLegacySummaryIntent(context: Context): PendingIntent? {
        val contentIntent = FetLifeWebViewActivity.createIntent(context, "notifications", true, true, R.id.navigation_bottom_notifications, true, null).apply {
            putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE, notificationType)
            putExtra(BaseActivity.EXTRA_NOTIFICATION_MERGE_ID, mergeId)
        }
        return PendingIntent.getActivity(context,notificationIdRange,contentIntent,PendingIntent.FLAG_CANCEL_CURRENT)
    }

}