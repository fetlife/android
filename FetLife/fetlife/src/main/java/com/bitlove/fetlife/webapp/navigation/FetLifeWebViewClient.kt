package com.bitlove.fetlife.webapp.navigation

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

class FetLifeWebViewClient : WebViewClient() {

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        //TODO(WEBAPP): add headers
        //request.requestHeaders.
        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        //TODO(WEBAPP): handle navigation
        return super.shouldOverrideUrlLoading(view, request)
    }
}