package com.bitlove.fetlife.webapp.communication

import android.content.Context
import android.webkit.JavascriptInterface
import com.bitlove.fetlife.webapp.kotlin.showToast

class WebViewInterface(private val context: Context) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        context.showToast(toast)
    }

}