package com.bitlove.fetlife.inbound.customtabs

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.bitlove.fetlife.util.UrlUtil

class CustomTabLauncherActivity : Activity() {

    companion object {
        private const val EXTRA_LAUNCH_URL = "EXTRA_LAUNCH_URL"
        private const val INSTANCE_BOOLEAN_URL_LAUNCHED = "INSTANCE_BOOLEAN_URL_LAUNCHED"
        fun closeCustomTab(context: Context) {
            var intent = Intent(context, CustomTabLauncherActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
        fun launchUrl(url: String, context: Context) {
            var intent = Intent(context, CustomTabLauncherActivity::class.java).apply {
                putExtra(EXTRA_LAUNCH_URL, url)
            }
            context.startActivity(intent)
        }
    }

    var customTabLaunched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customTabLaunched = savedInstanceState?.getBoolean(INSTANCE_BOOLEAN_URL_LAUNCHED) ?: false
    }

    override fun onResume() {
        super.onResume()
        val url = intent.getStringExtra(EXTRA_LAUNCH_URL)

        if (customTabLaunched || TextUtils.isEmpty(url)) {
            finish()
            return
        }

        customTabLaunched = true
        UrlUtil.openUrl(this, url, true, false);
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putBoolean(INSTANCE_BOOLEAN_URL_LAUNCHED,customTabLaunched)
        super.onSaveInstanceState(outState)
    }


}