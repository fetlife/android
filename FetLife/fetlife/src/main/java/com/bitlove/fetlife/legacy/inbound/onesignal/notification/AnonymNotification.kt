package com.bitlove.fetlife.legacy.inbound.onesignal.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bitlove.fetlife.common.app.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.common.app.StartActivity
import com.bitlove.fetlife.legacy.inbound.onesignal.NotificationParser

class AnonymNotification {

    companion object {
        var notificationId : Int = NotificationParser.NOTIFICATION_ID_ANONYM
    }

    fun display(fetLifeApplication: FetLifeApplication) {

        val channelId = NotificationParser.JSON_VALUE_TYPE_INFO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = fetLifeApplication.getString(R.string.settings_title_notification_info_enabled)
            val channelDescription = fetLifeApplication.getString(R.string.settings_summary_notification_info_enabled)
            val channelImportance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, channelImportance).apply { if (channelDescription != null) this.description = channelDescription }
            fetLifeApplication.getSystemService(NotificationManager::class.java)!!.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(fetLifeApplication, channelId).apply {
            setAutoCancel(true)
            setContentIntent(getContentIntent(fetLifeApplication))
            setVisibility(NotificationCompat.VISIBILITY_SECRET)

            setContentTitle(fetLifeApplication.getString(R.string.noification_title_anonym))
            setContentText(fetLifeApplication.getString(R.string.noification_text_anonym))

            setLargeIcon(BitmapFactory.decodeResource(fetLifeApplication.resources, R.mipmap.app_icon_vanilla))
            setSmallIcon(R.drawable.ic_anonym_notif_small)
            setLights(fetLifeApplication.userSessionManager.notificationColor, 1000, 1000)
            setSound(fetLifeApplication.userSessionManager.notificationRingtone)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setChannelId(channelId)
            }

            val vibrationSetting = fetLifeApplication.userSessionManager.notificationVibration
            if (vibrationSetting != null) {
                setVibrate(vibrationSetting)
            } else {
                setDefaults(Notification.DEFAULT_VIBRATE)
            }
        }

        val notificationManager = NotificationManagerCompat.from(fetLifeApplication)
        notificationManager.notify(notificationId,notificationBuilder.build());
    }

    private fun getContentIntent(context: Context): PendingIntent? {
        val contentIntent = Intent(context, StartActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK; }
        return PendingIntent.getActivity(context, 0, contentIntent, 0)
    }


}