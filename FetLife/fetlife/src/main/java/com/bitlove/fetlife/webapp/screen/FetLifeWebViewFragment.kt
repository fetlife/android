package com.bitlove.fetlife.webapp.screen

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.model.api.FetLifeService
import com.bitlove.fetlife.webapp.communication.WebViewInterface
import com.bitlove.fetlife.webapp.kotlin.getBooleanArgument
import com.bitlove.fetlife.webapp.kotlin.getStringArgument
import com.bitlove.fetlife.webapp.kotlin.showToast
import com.bitlove.fetlife.webapp.navigation.WebAppNavigation
import kotlinx.android.synthetic.main.tool_bar_default.*
import kotlinx.android.synthetic.main.tool_bar_default.view.*
import kotlinx.android.synthetic.main.webapp_fragment_webview.*
import kotlinx.android.synthetic.main.webapp_fragment_webview.view.*


class FetLifeWebViewFragment : Fragment() {
    
    companion object {
        private const val ARG_PAGE_URL = "ARG_PAGE_URL"
        private const val ARG_USE_TOP_BACK_NAVIGATION = "ARG_USE_TOP_BACK_NAVIGATION"

        fun newInstance(pageUrl: String, useTopBackNavigation: Boolean = false) : FetLifeWebViewFragment {
            return FetLifeWebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PAGE_URL,pageUrl)
                    putBoolean(ARG_USE_TOP_BACK_NAVIGATION,useTopBackNavigation)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.webapp_fragment_webview,container,false).apply {
            val url = getStringArgument(ARG_PAGE_URL)
            val navigationTitleId = FetLifeApplication.getInstance().webAppNavigation.getTitle(url)
            val navigationTitle = if (navigationTitleId != null) container?.context?.getString(navigationTitleId) else null
            toolbar_title.text = navigationTitle

            if (getBooleanArgument(ARG_USE_TOP_BACK_NAVIGATION) == true) {
                (activity as? FetLifeWebViewActivity)?.let {
                    it.setSupportActionBar(toolbar)
                    it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    it.supportActionBar?.setDisplayShowHomeEnabled(true)
                }
            }

            web_view.settings.javaScriptEnabled = true
            web_view.setBackgroundColor(Color.TRANSPARENT)
            web_view.addJavascriptInterface(WebViewInterface(context), "Android")
            web_view.webViewClient = object : WebViewClient() {
                override fun onPageFinished(webView: WebView?, url: String?) {
                    //web_view_progress_bar.visibility = View.GONE
                    dismissProgress()
                    val navigationTitleId = FetLifeApplication.getInstance().webAppNavigation.getTitle(url)
                    val navigationTitle = if (navigationTitleId != null) webView?.context?.getString(navigationTitleId) else null
                    toolbar_title.text = navigationTitle?: getWebViewTitle(webView)
                    if (webView?.tag == true) {
                        webView.tag = false
                        webView.clearHistory()
                    }
                    super.onPageFinished(webView, url)
                }

                private fun getWebViewTitle(webView: WebView?): String {
                    var title = webView?.title ?: ""
                    val separatorPos = title.indexOf(WebAppNavigation.WEB_TITLE_SEPARATOR)
                    if (separatorPos >= 0) {
                        title = title.substring(0, separatorPos)
                    }
                    val counterPos = title.indexOf(WebAppNavigation.WEB_COUNTER_SEPARATOR)
                    if (counterPos >= 0 && counterPos < title.length-1) {
                        title = title.substring(counterPos+1, separatorPos)
                    }
                    return title
                }

                override fun shouldOverrideUrlLoading(webView: WebView?, request: WebResourceRequest?): Boolean {
                    val navigated = FetLifeApplication.getInstance().webAppNavigation.navigate(request?.url, webView)
                    return if (navigated) {
                        true
                    } else {
                        //TODO(WEBAPP): remove App Ids
                        super.shouldOverrideUrlLoading(webView, request)
                    }
                }

                override fun shouldInterceptRequest(webView: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                    return super.shouldInterceptRequest(webView, request)
                }

                override fun onReceivedError(webView: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    super.onReceivedError(webView, request, error)
                    dismissProgress()
                    if (activity?.isFinishing != true) {
                        webView?.let {
                            it.context.showToast(getString(R.string.error_webview_failed))
                            it.clearCache(false)
                            it.clearHistory()
                            it.loadUrl("about:blank")
                        }
                    }
                }

                override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(webView, url, favicon)
                    showProgress()
                }
            }

            val headers = HashMap<String,String>()
            val accessToken = FetLifeApplication.getInstance().userSessionManager.currentUser.accessToken
            val authHeader = FetLifeService.AUTH_HEADER_PREFIX + accessToken
            headers.put("X-Fetlife-Webview", "1")
            headers.put("Authorization", authHeader)
            web_view.loadUrl(url,headers)
        }
    }

    private fun showProgress() {
        toolbar_progress_indicator.visibility = View.VISIBLE
    }

    private fun dismissProgress() {
        toolbar_progress_indicator.visibility = View.INVISIBLE
    }

    fun onKeyBack() : Boolean{
        return if (web_view.canGoBack()) {
            web_view.goBack()
            true
        } else {
            false
        }

    }

}