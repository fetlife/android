package com.bitlove.fetlife.inbound.onesignal.notification

import android.app.*
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.notification.NotificationParser
import com.bitlove.fetlife.view.screen.BaseActivity
import com.bitlove.fetlife.view.screen.resource.TurboLinksViewActivity
import org.json.JSONObject
import java.util.*

class QuestionAnsweredNotification(id: String, title: String, message: String, launchUrl: String, group: String, additionalData: JSONObject) : OneSignalNotification(NotificationParser.JSON_VALUE_TYPE_QUESTIONS_NEW_ANSWER, id, title, message, launchUrl, group, additionalData, R.string.settings_key_notification_new_answers_enabled) {

    companion object {
        private val liveNotifications: MutableList<QuestionAnsweredNotification> = Collections.synchronizedList(ArrayList())
        fun clearNotifications(groupingField: String) {
            liveNotifications.removeAll(liveNotifications.filter { it.launchUrl == groupingField })
        }
    }

    override fun display(fetLifeApplication: FetLifeApplication) {
        liveNotifications.add(this)

        val sumTitle = if (liveNotifications.size == 1) fetLifeApplication.getString(R.string.noification_summary_title_new_answer) else fetLifeApplication.getString(R.string.noification_summary_title_new_answers, liveNotifications.size)

        val summaryNotificationBuilder = getSummaryNotificationBuilder(fetLifeApplication,null,sumTitle,sumTitle, liveNotifications,{it.launchUrl},
                { context: Context, _, count: Int ->
                    if (count == 1) context.getString(R.string.noification_text_new_answer) else context.getString(R.string.noification_text_new_answers,count)
                }
        )

        val notificationManager = NotificationManagerCompat.from(fetLifeApplication)
        notificationManager.notify(com.bitlove.fetlife.notification.OneSignalNotification.NOTIFICATION_ID_ANSWERS, summaryNotificationBuilder.build())

        var i = OneSignalNotification.NOTIFICATION_ID_ANSWERS + 1
        for (oneSignalNotification in liveNotifications.groupBy { it.launchUrl }) {
            notificationManager.notify(i++, getNotification(fetLifeApplication,oneSignalNotification.value.first(),i++,oneSignalNotification.value.size))
        }
    }

    private fun getNotification(fetLifeApplication: FetLifeApplication, oneSignalNotification: QuestionAnsweredNotification, order: Int, count: Int): Notification {
        val title = fetLifeApplication.getString(R.string.noification_title_new_answers)
        val text = if (count == 1) fetLifeApplication.getString(R.string.noification_text_new_answer) else fetLifeApplication.getString(R.string.noification_text_new_answers, count)
        val contentIntent = getContentIntent(fetLifeApplication, oneSignalNotification.launchUrl, order)
        return getDefaultNotificationBuilder(fetLifeApplication,contentIntent,title,text).build()
    }

    private fun getContentIntent(context: Context, launchUrl: String, order: Int): PendingIntent? {
        val baseIntent = TurboLinksViewActivity.createIntent(context, "q", context.getString(R.string.title_activity_questions), true, TurboLinksViewActivity.FAB_LINK_NEW_QUESTION, false)
        val contentIntent = TurboLinksViewActivity.createIntent(context, launchUrl.replace("//fetlife.com".toRegex(), "//app.fetlife.com"), null, false, null, false)
        contentIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE, notificationType)
        contentIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_GROUP_ID, launchUrl)
        return TaskStackBuilder.create(context).addNextIntentWithParentStack(baseIntent).addNextIntent(contentIntent).getPendingIntent(order, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createNotificationChannel(context: Context): String {
        val channelId = NOTIFICATION_CHANNEL_QUESTIONS
        val name = context.getString(R.string.notification_chanel_name_questions)
        val descriptionText = context.getString(R.string.notification_chanel_description_questions)
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, name, importance).apply { this.description = descriptionText }
        context.getSystemService(NotificationManager::class.java)!!.createNotificationChannel(channel)
        return channelId
    }
}