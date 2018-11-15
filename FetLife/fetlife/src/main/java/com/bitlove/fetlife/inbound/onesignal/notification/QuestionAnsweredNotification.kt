package com.bitlove.fetlife.inbound.onesignal.notification

import android.app.*
import android.content.Context
import com.bitlove.fetlife.R
import com.bitlove.fetlife.view.screen.BaseActivity
import com.bitlove.fetlife.view.screen.resource.TurboLinksViewActivity
import org.json.JSONObject

class QuestionAnsweredNotification(notificationType: String, notificationIdRange: Int, title: String, message: String, launchUrl: String, mergeId: String?, collapseId: String?, additionalData: JSONObject, preferenceKey: String?) : OneSignalNotification(notificationType, notificationIdRange, title, message, launchUrl, mergeId, collapseId, additionalData, preferenceKey) {

    override fun getNotificationChannelName(context: Context): String? {
        return context.getString(R.string.notification_channel_name_questions)
    }

    override fun getNotificationChannelDescription(context: Context): String? {
        return context.getString(R.string.notification_channel_description_questions)
    }

    override fun getSummaryTitle(notificationCount: Int, context: Context): String? {
        return context.resources.getQuantityString(R.plurals.noification_summary_title_questions_new_answer, notificationCount, notificationCount)
    }

    override fun getSummaryText(notificationCount: Int, context: Context): String? {
        return context.getString(R.string.noification_summary_text_questions_new_answer)
    }

    override fun getNotificationTitle(oneSignalNotification: OneSignalNotification, notificationCount: Int, context: Context): String {
        return context.resources.getQuantityString(R.plurals.noification_summary_title_questions_new_answer, notificationCount, notificationCount)
    }

    override fun getNotificationIntent(oneSignalNotification: OneSignalNotification, context: Context, order: Int): PendingIntent? {
        val baseIntent = TurboLinksViewActivity.createIntent(context, "q", context.getString(R.string.title_activity_questions), true, TurboLinksViewActivity.FAB_LINK_NEW_QUESTION, false)
        val contentIntent = TurboLinksViewActivity.createIntent(context, oneSignalNotification.launchUrl.replace("//fetlife.com".toRegex(), "//app.fetlife.com"), null, false, null, false)
        contentIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE, notificationType)
        contentIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_MERGE_ID, mergeId)
        //return PendingIntent.getActivity(context, order, contentIntent, PendingIntent.FLAG_IMMUTABLE)
        return TaskStackBuilder.create(context).addNextIntentWithParentStack(baseIntent).addNextIntent(contentIntent).getPendingIntent(order, PendingIntent.FLAG_CANCEL_CURRENT)
    }

}