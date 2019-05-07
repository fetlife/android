package com.bitlove.fetlife.legacy.inbound.onesignal.notification

import com.bitlove.fetlife.common.app.FetLifeApplication
import org.json.JSONObject

class UnknownNotification(title: String, message: String, launchUrl: String, additionalData: JSONObject) : OneSignalNotification("unknown", Int.MIN_VALUE, title, message, launchUrl, null, null, additionalData, null) {

    //Hide notification
    override fun handle(fetLifeApplication: FetLifeApplication): Boolean {
        return true
    }

}