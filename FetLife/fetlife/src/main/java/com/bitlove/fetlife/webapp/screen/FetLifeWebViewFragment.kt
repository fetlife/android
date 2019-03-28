package com.bitlove.fetlife.webapp.screen

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import com.bitlove.fetlife.BuildConfig
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.event.ServiceCallFailedEvent
import com.bitlove.fetlife.event.ServiceCallFinishedEvent
import com.bitlove.fetlife.event.ServiceCallStartedEvent
import com.bitlove.fetlife.model.api.FetLifeService
import com.bitlove.fetlife.model.service.FetLifeApiIntentService
import com.bitlove.fetlife.util.LogUtil
import com.bitlove.fetlife.util.ServerIdUtil
import com.bitlove.fetlife.util.VersionUtil
import com.bitlove.fetlife.webapp.communication.WebViewInterface
import com.bitlove.fetlife.webapp.kotlin.getBooleanArgument
import com.bitlove.fetlife.webapp.kotlin.getStringArgument
import com.bitlove.fetlife.webapp.kotlin.showToast
import com.bitlove.fetlife.webapp.navigation.WebAppNavigation
import kotlinx.android.synthetic.main.tool_bar_default.*
import kotlinx.android.synthetic.main.tool_bar_default.view.*
import kotlinx.android.synthetic.main.webapp_fragment_webview.*
import kotlinx.android.synthetic.main.webapp_fragment_webview.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


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
            web_view.webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    if (BuildConfig.DEBUG) {
                        Log.d("[WEBVIEW][CONSOLE]",consoleMessage?.toString())
                    }
                    return super.onConsoleMessage(consoleMessage)
                }
            }
            web_view.webViewClient = object : WebViewClient() {
                override fun onPageFinished(webView: WebView?, url: String?) {
                    LogUtil.writeLog("onPageFinished($url)")
                    //web_view_progress_bar.visibility = View.GONE
                    dismissProgress()
                    val navigationTitleId = FetLifeApplication.getInstance().webAppNavigation.getTitle(url)
                    val navigationTitle = if (navigationTitleId != null) webView?.context?.getString(navigationTitleId) else null
                    toolbar_title.text = navigationTitle?: getWebViewTitle(webView)
                    if (webView?.tag == true) {
                        webView.tag = false
                        webView.clearHistory()
                    }
                    url?.let {
                        FetLifeApplication.getInstance().actionCable.tryConnect(context,url)
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
                    val extraPos = title.indexOf(WebAppNavigation.WEB_EXTRA_SEPARATOR)
                    if (extraPos >= 0) {
                        title = title.substring(0, extraPos)
                    }
                    return title
                }

                override fun shouldOverrideUrlLoading(webView: WebView?, request: WebResourceRequest?): Boolean {
                    LogUtil.writeLog("shouldOverrideUrlLoading(${request?.url})")
                    val navigated = FetLifeApplication.getInstance().webAppNavigation.navigate(request?.url, webView, activity)
                    return if (navigated) {
                        true
                    } else {
                        //TODO(WEBAPP): remove App Ids
                        super.shouldOverrideUrlLoading(webView, request)
                    }
                }

                override fun onPageCommitVisible(webView: WebView?, url: String?) {
                    LogUtil.writeLog("onPageCommitVisible($url)")
                    super.onPageCommitVisible(webView, url)
                    val navigationTitleId = FetLifeApplication.getInstance().webAppNavigation.getTitle(url)
                    val navigationTitle = if (navigationTitleId != null) webView?.context?.getString(navigationTitleId) else null
                    toolbar_title.text = navigationTitle?: getWebViewTitle(webView)
                }

                override fun onReceivedError(webView: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        LogUtil.writeLog("onReceivedError(${request?.url},${error?.errorCode}:${error?.description})")
                    } else {
                        LogUtil.writeLog("onReceivedError(${request?.url},${error?.toString()}")
                    }
                    super.onReceivedError(webView, request, error)
                    dismissProgress()
                    if (activity?.isFinishing == false) {
                        webView?.let {
                            it.context.showToast(getString(R.string.error_webview_failed))
                            it.clearCache(false)
                            it.clearHistory()
                            it.loadUrl("about:blank")
                        }
                    }
                }

                override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
                    LogUtil.writeLog("onPageStarted($url)")
                    super.onPageStarted(webView, url, favicon)
                    showProgress()
                }
            }

            val headers = HashMap<String,String>()
            val accessToken = FetLifeApplication.getInstance().userSessionManager.currentUser.accessToken
            val authHeader = FetLifeService.AUTH_HEADER_PREFIX + accessToken
            headers.put("X-Fetlife-Webview", "1")
            headers.put("X-Fetlife-Android", VersionUtil.getCurrentVersionInt(context).toString())
            headers.put("Authorization", authHeader)
            web_view.loadUrl(url,headers)
            url?.let {
                FetLifeApplication.getInstance().actionCable.tryConnect(context,url)
            }
        }
    }

    private fun showProgress() {
        toolbar_progress_indicator?.visibility = View.VISIBLE
    }

    private fun dismissProgress() {
        toolbar_progress_indicator?.visibility = View.INVISIBLE
    }

    //Media backup load
    override fun onStart() {
        super.onStart()
        FetLifeApplication.getInstance().eventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        FetLifeApplication.getInstance().eventBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResourceListCallStarted(serviceCallStartedEvent: ServiceCallStartedEvent) {
        if (isRelatedCall(serviceCallStartedEvent.serviceCallAction, serviceCallStartedEvent.params)) {
            showProgress()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun callFinished(serviceCallFinishedEvent: ServiceCallFinishedEvent) {
        if (!isRelatedCall(FetLifeApiIntentService.getActionInProgress(), FetLifeApiIntentService.getInProgressActionParams())) {
            dismissProgress()
        }
        if (isRelatedCall(serviceCallFinishedEvent.serviceCallAction, serviceCallFinishedEvent.params)) {
            var url = serviceCallFinishedEvent.params[2]
            if (url != web_view.url) {
                //user navigated further
                //TODO(WEBAPP): think about edge case when request initiated earlier, different place
                return
            }

            var mediaId = serviceCallFinishedEvent.params[1]
            if (ServerIdUtil.isServerId(mediaId)) {
                mediaId = ServerIdUtil.getLocalId(mediaId)
            }

            if (serviceCallFinishedEvent.serviceCallAction === FetLifeApiIntentService.ACTION_APICALL_MEMBER_PICTURE) {
                FetLifeApplication.getInstance().webAppNavigation.showPicture(context,mediaId)
            } else if (serviceCallFinishedEvent.serviceCallAction === FetLifeApiIntentService.ACTION_APICALL_MEMBER_VIDEO) {
                FetLifeApplication.getInstance().webAppNavigation.showVideo(context,mediaId)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun callFailed(serviceCallFailedEvent: ServiceCallFailedEvent) {
        if (isRelatedCall(serviceCallFailedEvent.serviceCallAction, serviceCallFailedEvent.params)) {
            dismissProgress()
        }
    }

    private fun isRelatedCall(serviceCallAction: String?, params: Array<String>?): Boolean {
        if (params == null || params.size < 3) {
            return false
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_PICTURE == serviceCallAction) {
            return true
        }
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER_VIDEO == serviceCallAction
    }

    fun onKeyBack() : Boolean{
        return if (web_view.canGoBack()) {
            web_view.goBack()
            true
        } else {
            false
        }

    }

    fun getFabLink(): String? {
        //TODO(WEBAPP): implement to be more dynamic
        return FetLifeApplication.getInstance().webAppNavigation.getFabLink(getStringArgument(ARG_PAGE_URL))
    }

}