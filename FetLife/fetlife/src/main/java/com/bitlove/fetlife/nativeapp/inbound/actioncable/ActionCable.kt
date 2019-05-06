package com.bitlove.fetlife.nativeapp.inbound.actioncable

import android.content.Context
import android.text.TextUtils
import android.webkit.CookieManager
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.nativeapp.event.NotificationCountUpdatedEvent
import com.bitlove.fetlife.nativeapp.session.UserSessionManager
import com.crashlytics.android.Crashlytics
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.hosopy.actioncable.ActionCable
import com.hosopy.actioncable.Channel
import com.hosopy.actioncable.Consumer
import java.net.URI
import java.util.*

class ActionCable {

    companion object {
        const val PREF_KEY_ACTION_CABLE_COOKIES = "PREF_KEY_ACTION_CABLE_COOKIES"
    }

    private var connected = false
    private var actionCableConsumer: Consumer? = null

    fun disconnect() {
        actionCableConsumer?.disconnect()
        connected = false
    }

    fun tryConnect(context: Context, url: String) {

        if (connected) {
            return
        }

        val options = Consumer.Options()

        var cookies: String?
        try {
            cookies = CookieManager.getInstance().getCookie(url)
        } catch (t: Throwable) {cookies = null}

        if (TextUtils.isEmpty(cookies)) {
            return
        }

        val headers = HashMap<String,String>()
        headers.put("Cookie", cookies!!)
        headers.put("Origin", "https://fetlife.com")
        headers.put("Access-Control-Allow-Origin", "https://fetlife.com")
        options.headers = headers

        actionCableConsumer = ActionCable.createConsumer(URI.create("wss://ws.fetlife.com/cable"), options)

        val appearanceChannel = Channel("NotificationsChannel")
        val subscription = actionCableConsumer!!.getSubscriptions().create(appearanceChannel)

        subscription
                .onConnected{connected = true}
                .onRejected{Crashlytics.logException(Exception("ActionCable rejected"))}
                .onReceived{jsonElement -> sendNotificationUpdatedEvent(jsonElement)}
                .onDisconnected{connected = false}
                .onFailed{ex -> Crashlytics.logException(ex)}

        actionCableConsumer!!.connect()

    }

    private fun sendNotificationUpdatedEvent(jsonElement: JsonElement?) {
        var jsonObject = jsonElement as? JsonObject
        var type = jsonObject?.get("type")?.asString
        var count = jsonObject?.get("count")?.asInt
        if (count == null || type == null) {
            return
        }

        val notifEvent = NotificationCountUpdatedEvent()

        var preferenceKey: String
        when(type) {
            "new_messages_count_updated" -> {notifEvent.messagesCount = count; preferenceKey = UserSessionManager.PREF_KEY_MESSAGE_COUNT}
            "friendship_requests_count_updated" -> {notifEvent.requestCount = count; preferenceKey = UserSessionManager.PREF_KEY_REQUEST_COUNT}
            "notifications_count_updated" -> {notifEvent.notificationCount = count; preferenceKey = UserSessionManager.PREF_KEY_NOTIF_COUNT}
            else -> return
        }

        val prefs = FetLifeApplication.getInstance().userSessionManager.activeUserPreferences ?: return
        val currentCount = prefs.getInt(preferenceKey,-1);

        if (currentCount == count) {
            return
        }

        prefs.edit().putInt(preferenceKey,count).apply();

        FetLifeApplication.getInstance().eventBus.post(notifEvent)
    }

    fun isConnected(): Boolean {
        return connected
    }

}