package com.bitlove.fetlife.inbound.onesignal.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.model.pojos.fetlife.db.NotificationHistoryItem
import com.bitlove.fetlife.util.AppUtil
import org.json.JSONObject

abstract class OneSignalNotification(val notificationType: String,
                                     val id: String,
                                     val title: String,
                                     val message: String,
                                     val launchUrl: String,
                                     val group: String,
                                     val additionalData: JSONObject,
                                     val preferenceKey: Int?) {

    companion object {
        //Collapse Ids
        const val NOTIFICATION_ID_DO_NOT_COLLAPSE = -1

        //Notification Ids ranges
        const val NOTIFICATION_ID_ANONYM = 100
        const val NOTIFICATION_ID_FRIEND_REQUEST = 200
        const val NOTIFICATION_ID_MESSAGE = 300
        const val NOTIFICATION_ID_LOVE = 400
        const val NOTIFICATION_ID_COMMENT = 500
        const val NOTIFICATION_ID_MENTION = 600
        const val NOTIFICATION_ID_GROUP = 700
        const val NOTIFICATION_ID_ANSWERS = 800
        const val NOTIFICATION_ID_INFO_INTERVAL = 10000


        const val NOTIFICATION_CHANNEL_DEFUALT = "NOTIFICATION_CHANNEL_DEFUALT"
        const val NOTIFICATION_CHANNEL_QUESTIONS = "NOTIFICATION_CHANNEL_QUESTIONS"

        //Launch target string parts
        const val LAUNCH_URL_PARAM_SEPARATOR = ":"
        const val LAUNCH_URL_PREFIX = "FetLifeApp://"
    }

    open fun handle(fetLifeApplication: FetLifeApplication): Boolean = false

    open fun display(fetLifeApplication: FetLifeApplication) {}

    fun isEnabled(fetLifeApplication: FetLifeApplication): Boolean {
        if (preferenceKey == null) return false
        return fetLifeApplication.userSessionManager.activeUserPreferences.getBoolean(fetLifeApplication.getString(preferenceKey), true)
    }

    protected fun getSummaryNotificationBuilder(fetLifeApplication: FetLifeApplication,
                                                contentIntent: PendingIntent?,
                                                title: String,
                                                text: String,
                                                oneSignalNotification: List<OneSignalNotification>,
                                                keySelector: (OneSignalNotification) -> String,
                                                notificationTitleFun: (Context, OneSignalNotification, Int) -> String): NotificationCompat.Builder {
        return getDefaultNotificationBuilder(fetLifeApplication,contentIntent,title,text).apply {
            setGroupSummary(true)

            val inboxStyle = NotificationCompat.InboxStyle()

            val groupedNotifications = oneSignalNotification.groupBy{ keySelector(it) }
            for (groupedNotification in groupedNotifications) {
                inboxStyle.addLine(notificationTitleFun(fetLifeApplication,groupedNotification.value.first(),groupedNotification.value.size))
            }

            inboxStyle.setBigContentTitle(title)
            inboxStyle.setSummaryText(text)
            setStyle(inboxStyle)
        }
    }

    protected fun getDefaultNotificationBuilder(fetLifeApplication: FetLifeApplication, contentIntent: PendingIntent?, title: String, text: String): NotificationCompat.Builder {
        val notificationChannelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(fetLifeApplication)
        } else {
            NOTIFICATION_CHANNEL_DEFUALT
        }
        return NotificationCompat.Builder(fetLifeApplication, notificationChannelId).apply {
            setAutoCancel(true)

            setContentTitle(title)
            setContentText(text)
            setContentIntent(contentIntent)
            setGroup(notificationType)

            setLargeIcon(BitmapFactory.decodeResource(fetLifeApplication.resources, R.mipmap.app_icon_kinky))
            setSmallIcon(R.drawable.ic_stat_onesignal_default)
            setLights(fetLifeApplication.userSessionManager.notificationColor, 1000, 1000)
            setSound(fetLifeApplication.userSessionManager.notificationRingtone)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setChannelId(NOTIFICATION_CHANNEL_DEFUALT)
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

    @RequiresApi(Build.VERSION_CODES.O)
    protected open fun createNotificationChannel(context: Context): String {
        val channelId = NOTIFICATION_CHANNEL_DEFUALT
        val name = context.getString(R.string.notification_chanel_name_default)
        val descriptionText = context.getString(R.string.notification_chanel_description_default)
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, name, importance).apply { this.description = descriptionText }
        context.getSystemService(NotificationManager::class.java)!!.createNotificationChannel(channel)
        return channelId
    }

    protected fun createNotificationItem(notificationId: Int, collapseId: String): NotificationHistoryItem {
        return NotificationHistoryItem().apply {
            this.displayId = notificationId
            this.displayHeader = this@OneSignalNotification.title
            this.displayMessage = this@OneSignalNotification.message
            this.launchUrl = this@OneSignalNotification.launchUrl
            this.collapseId = collapseId
            this.timeStamp = try {
                (this@OneSignalNotification.additionalData.getDouble("sent_at") * 1000).toLong()
            } catch (exception: Throwable) {
                -1
            }
        }
    }
}