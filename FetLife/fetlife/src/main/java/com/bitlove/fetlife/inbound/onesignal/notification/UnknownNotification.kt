package com.bitlove.fetlife.inbound.onesignal.notification

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.notification.NotificationParser
import org.json.JSONObject

class UnknownNotification(id: String, title: String, message: String, launchUrl: String, group: String, additionalData: JSONObject) : OneSignalNotification("unknown", id, title, message, launchUrl, group, additionalData, -1) {

    //Hide notification
    override fun handle(fetLifeApplication: FetLifeApplication): Boolean {
        return true
    }

}