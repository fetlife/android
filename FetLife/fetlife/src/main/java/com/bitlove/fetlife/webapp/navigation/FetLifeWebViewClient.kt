package com.bitlove.fetlife.webapp.navigation

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.api.FetLifeService

@Deprecated
class FetLifeWebViewClient : WebViewClient() {

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
//        val accessToken = FetLifeApplication.getInstance().userSessionManager.currentUser.accessToken
//        val authHeader = FetLifeService.AUTH_HEADER_PREFIX + accessToken
//        request?.requestHeaders?.put("X-Fetlife-Webview", "1")
//        request?.requestHeaders?.put("Authorization", authHeader)
        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        //TODO(WEBAPP): handle navigation
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
    }

}