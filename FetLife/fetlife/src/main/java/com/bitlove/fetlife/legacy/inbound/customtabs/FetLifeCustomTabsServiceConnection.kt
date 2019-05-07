package com.bitlove.fetlife.legacy.inbound.customtabs

import android.content.ComponentName
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import com.bitlove.fetlife.common.app.FetLifeApplication

class FetLifeCustomTabsServiceConnection : CustomTabsServiceConnection() {

    private var customTabCallback = object : CustomTabsCallback() {
        override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
            if (navigationEvent == CustomTabsCallback.NAVIGATION_FINISHED) {
                FetLifeApplication.getInstance().onCustomTabFinishedNavigation()
            }
        }
    }

    override fun onCustomTabsServiceConnected(name: ComponentName?, client: CustomTabsClient?) {
        client?.warmup(0)
        var session = client?.newSession(customTabCallback)
        FetLifeApplication.getInstance().customTabsSession = session
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        FetLifeApplication.getInstance().customTabsSession = null
    }

}