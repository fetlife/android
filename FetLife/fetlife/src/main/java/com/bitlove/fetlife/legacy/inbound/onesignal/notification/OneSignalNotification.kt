package com.bitlove.fetlife.legacy.inbound.onesignal.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.legacy.model.pojos.fetlife.db.NotificationHistoryItem
import com.bitlove.fetlife.legacy.util.AppUtil
import com.bitlove.fetlife.legacy.util.NotificationUtil
import org.json.JSONObject

//TODO anonym, deleteintent, inner launches, move hardcoded urls
abstract class OneSignalNotification(val notificationType: String,
                                     val notificationIdRange: Int,
                                     val title: String?,
                                     val message: String?,
                                     val launchUrl: String?,
                                     val mergeId: String?,
                                     val collapseId: String?,
                                     val additionalData: JSONObject?,
                                     private val preferenceKey: String?) {

    open fun getNotificationChannelId(): String = notificationType
    open fun getNotificationChannelName(context: Context): String? = null
    open fun getNotificationChannelDescription(context: Context): String? = null
    open fun getNotificationChannelImportance(): Int = NotificationManager.IMPORTANCE_DEFAULT

    open fun getSummaryTitle(notificationCount: Int, context: Context): String? = null
    open fun getSummaryText(notificationCount: Int, context: Context): String? = null

    open fun getNotificationTitle(oneSignalNotification: OneSignalNotification, count: Int, context: Context): String? = oneSignalNotification.title
    open fun getNotificationText(oneSignalNotification: OneSignalNotification, count: Int, context: Context): String? = oneSignalNotification.message
    open fun getNotificationIntent(oneSignalNotification: OneSignalNotification, context: Context, order: Int): PendingIntent? = null
    open fun getLegacySummaryIntent(context: Context): PendingIntent? = getNotificationIntent(this,context,notificationIdRange)

    open fun getNotificationItemLaunchUrl(): String? = launchUrl

    open fun handle(fetLifeApplication: FetLifeApplication): Boolean = false

    open fun display(fetLifeApplication: FetLifeApplication) {

        val groupingEnabled = fetLifeApplication.userSessionManager.activeUserPreferences.getBoolean(fetLifeApplication.getString(R.string.settings_key_notification_grouping_enabled),true);

        //Selecting storage
        var liveNotifications = liveNotificationMap[notificationType] ?: ArrayList()
        if (liveNotifications.isEmpty()) liveNotificationMap[notificationType] = liveNotifications

        //Create notification channel
        val channelId = getNotificationChannelId()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getNotificationChannelName(fetLifeApplication)
            val channelDescription = getNotificationChannelDescription(fetLifeApplication)
            val channelImportance = getNotificationChannelImportance()

            val channel = NotificationChannel(channelId, channelName, channelImportance).apply { if (channelDescription != null) this.description = channelDescription }
            fetLifeApplication.getSystemService(NotificationManager::class.java)!!.createNotificationChannel(channel)
        }

        addToLiveNotifications(liveNotifications, fetLifeApplication)

        //Sending notifications
        val notificationManager = NotificationManagerCompat.from(fetLifeApplication)

        val summaryTitle = getSummaryTitle(liveNotifications.size,fetLifeApplication)
        val summaryText = getSummaryText(liveNotifications.size,fetLifeApplication)

        val inboxStyle = NotificationCompat.InboxStyle()
                .setBigContentTitle(summaryTitle)
                .setSummaryText(summaryText)

        var i = notificationIdRange + 1
        for (oneSignalNotificationEntry in liveNotifications.groupBy { it.mergeId }) {
            val groupCount = oneSignalNotificationEntry.value.size
            val referenceNotification = oneSignalNotificationEntry.value.first()
            val title = getNotificationTitle(referenceNotification,groupCount,fetLifeApplication)
            val text = getNotificationText(referenceNotification,groupCount,fetLifeApplication)
            val pendingIntent = getNotificationIntent(referenceNotification, fetLifeApplication, i++)

            val groupId = if (groupingEnabled) notificationType else null
            val groupedNotification = getDefaultNotificationBuilder(channelId, groupId, fetLifeApplication, pendingIntent, title, text).build()
            notificationManager.notify(i++, groupedNotification)
            inboxStyle.addLine("$title $text")
        }

        val summaryNotification = getDefaultNotificationBuilder(channelId, notificationType, fetLifeApplication, null, summaryTitle, summaryText). apply {
            setGroupSummary(true)
            setStyle(inboxStyle)
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                setContentIntent(getLegacySummaryIntent(fetLifeApplication))
            }
        }.build()
        if (groupingEnabled) {
            notificationManager.notify(notificationIdRange, summaryNotification)
        }

        //Saving notification item
        saveNotificationItem(notificationIdRange)
    }

    open fun addToLiveNotifications(liveNotifications: MutableList<OneSignalNotification>, fetLifeApplication: FetLifeApplication) {
        //Collapsing notification with same collapseId
        if (collapseId != null) {
            val collapseIndex = liveNotifications.withIndex().firstOrNull { it.value.collapseId == collapseId }?.index
            if (collapseIndex != null) NotificationUtil.cancelNotification(fetLifeApplication, notificationIdRange + collapseIndex + 1)
        }
        liveNotifications.add(this)
    }

    open fun isEnabled(fetLifeApplication: FetLifeApplication): Boolean {
        if (preferenceKey == null) return false
        return fetLifeApplication.userSessionManager.activeUserPreferences.getBoolean(preferenceKey, true)
    }


    protected fun getDefaultNotificationBuilder(notificationChannelId: String, groupId: String?, fetLifeApplication: FetLifeApplication, contentIntent: PendingIntent?, title: String?, text: String?): NotificationCompat.Builder {
        val builder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) NotificationCompat.Builder(fetLifeApplication, notificationChannelId) else NotificationCompat.Builder(fetLifeApplication)
        return builder.apply {
            setAutoCancel(true)
            setContentIntent(contentIntent)

            setContentTitle(title)
            setContentText(text)

            setGroup(groupId)
            setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)

            setLargeIcon(BitmapFactory.decodeResource(fetLifeApplication.resources, R.mipmap.app_icon_kinky))
            setSmallIcon(R.drawable.ic_stat_onesignal_default)
            setLights(fetLifeApplication.userSessionManager.notificationColor, 1000, 1000)
            setSound(fetLifeApplication.userSessionManager.notificationRingtone)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setChannelId(notificationChannelId)
            }

            if (AppUtil.useAnonymNotifications(FetLifeApplication.getInstance())) {
                setVisibility(NotificationCompat.VISIBILITY_SECRET)
            } else {
                setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            }

            val vibrationSetting = fetLifeApplication.userSessionManager.notificationVibration
            if (vibrationSetting != null) {
                setVibrate(vibrationSetting)
            } else {
                setDefaults(Notification.DEFAULT_VIBRATE)
            }
        }
    }

    open fun saveNotificationItem(notificationId: Int) {
        NotificationHistoryItem().apply {
            this.displayId = notificationId
            this.displayHeader = this@OneSignalNotification.title
            this.displayMessage = this@OneSignalNotification.message
            this.launchUrl = getNotificationItemLaunchUrl()
            this.collapseId = this@OneSignalNotification.collapseId
            this.timeStamp = try {
                (this@OneSignalNotification.additionalData!!.getDouble("sent_at") * 1000)?.toLong()
            } catch (exception: Throwable) {
                -1
            }
        }.save()
    }

    companion object {
        const val NOTIFICATION_CHANNEL_DEFUALT = "NOTIFICATION_CHANNEL_DEFUALT"

        //Launch target string parts
        const val LAUNCH_URL_PARAM_SEPARATOR = ":"
        const val LAUNCH_URL_PREFIX = "FetLifeApp://"

        val liveNotificationMap : MutableMap<String,MutableList<OneSignalNotification>> = HashMap()

        fun clearNotifications(notificationType: String? = null, mergeId: String? = null) {
            if (notificationType == null) {
                liveNotificationMap.clear()
            }
            val liveNotifications = liveNotificationMap[notificationType] ?: return
            if (mergeId == null) {
                liveNotificationMap.remove(notificationType)
            } else {
                liveNotifications.removeAll { it.mergeId == mergeId }
            }
        }

    }

}