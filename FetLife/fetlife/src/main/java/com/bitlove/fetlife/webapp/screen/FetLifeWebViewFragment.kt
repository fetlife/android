package com.bitlove.fetlife.webapp.screen

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.*
import androidx.fragment.app.Fragment
import com.bitlove.fetlife.BuildConfig
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.nativeapp.event.ServiceCallFailedEvent
import com.bitlove.fetlife.nativeapp.event.ServiceCallFinishedEvent
import com.bitlove.fetlife.nativeapp.event.ServiceCallStartedEvent
import com.bitlove.fetlife.nativeapp.model.api.FetLifeService
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService
import com.bitlove.fetlife.nativeapp.util.ServerIdUtil
import com.bitlove.fetlife.nativeapp.util.VersionUtil
import com.bitlove.fetlife.nativeapp.view.screen.BaseActivity
import com.bitlove.fetlife.webapp.communication.WebViewInterface
import com.bitlove.fetlife.webapp.kotlin.getBooleanArgument
import com.bitlove.fetlife.webapp.kotlin.getStringArgument
import com.bitlove.fetlife.webapp.navigation.WebAppNavigation
import com.crashlytics.android.Crashlytics
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
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.webapp_fragment_webview,container,false).apply {
            val url = getStringArgument(ARG_PAGE_URL)
            val navigationTitleId = FetLifeApplication.getInstance().webAppNavigation.getTitle(url)
            val navigationTitle = if (navigationTitleId != null) container?.context?.getString(navigationTitleId) else null
            toolbar_title.text = navigationTitle?.trim()

            if (getBooleanArgument(ARG_USE_TOP_BACK_NAVIGATION) == true) {
                (activity as? FetLifeWebViewActivity)?.let {
                    it.setSupportActionBar(toolbar)
                    it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    it.supportActionBar?.setDisplayShowHomeEnabled(true)
                    it.supportActionBar?.setDisplayShowTitleEnabled(false)
                }
            } else {
                (activity as? FetLifeWebViewActivity)?.let {
                    it.setSupportActionBar(toolbar)
                    it.supportActionBar?.setDisplayShowTitleEnabled(false)
                }
            }

            web_view.settings.javaScriptEnabled = true
            web_view.setBackgroundColor(Color.TRANSPARENT)
            web_view.addJavascriptInterface(WebViewInterface(context), "Android")
            web_view.settings.cacheMode = WebSettings.LOAD_NO_CACHE
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
                    //web_view_progress_bar.visibility = View.GONE
                    dismissProgress()
                    val navigationTitleId = FetLifeApplication.getInstance().webAppNavigation.getTitle(url)
                    val navigationTitle = if (navigationTitleId != null) webView?.context?.getString(navigationTitleId) else null
                    toolbar_title.text = (navigationTitle?: getWebViewTitle(webView))?.trim()
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
                        title = title.substring(counterPos+1)
                    }
                    val extraPos = title.indexOf(WebAppNavigation.WEB_EXTRA_SEPARATOR)
                    if (extraPos >= 0) {
                        title = title.substring(0, extraPos)
                    }
                    return title
                }

                override fun shouldOverrideUrlLoading(webView: WebView?, request: WebResourceRequest?): Boolean {
                    request ?: return false
                    val navigated = FetLifeApplication.getInstance().webAppNavigation.navigate(request, webView, activity as BaseActivity)
                    return if (navigated) {
                        true
                    } else {
                        request?.url.toString().let {
                            webView?.loadUrl(it,createRequestHeaders())
                            webView?.postDelayed(Runnable{activity?.invalidateOptionsMenu()},100)
                        }
                        true
                    }
                }

                override fun onPageCommitVisible(webView: WebView?, url: String?) {
                    super.onPageCommitVisible(webView, url)
                    val navigationTitleId = FetLifeApplication.getInstance().webAppNavigation.getTitle(url)
                    val navigationTitle = if (navigationTitleId != null) webView?.context?.getString(navigationTitleId) else null
                    toolbar_title.text = (navigationTitle?: getWebViewTitle(webView))?.trim()
                }

                override fun onReceivedError(webView: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    super.onReceivedError(webView, request, error)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Crashlytics.logException(Exception("request: ${request?.url}, error: ${error?.errorCode}-${error?.description}"))
                    } else {
                        Crashlytics.logException(Exception("onReceivedError"))
                    }
//                    dismissProgress()
//                    if (activity?.isFinishing != true) {
//                        webView?.let {
//                            it.context.showToast(getString(R.string.error_webview_failed))
//                            it.clearCache(false)
//                            it.clearHistory()
//                            it.loadUrl("about:blank")
//                        }
//                    }
                }

                override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(webView, url, favicon)
                    showProgress()
                }
            }

            web_view.loadUrl(url,createRequestHeaders())
            url?.let {
                FetLifeApplication.getInstance().actionCable.tryConnect(context,url)
            }
        }
    }

    private fun createRequestHeaders(): MutableMap<String, String>? {
        return HashMap<String,String>().apply {
            val accessToken = FetLifeApplication.getInstance().userSessionManager.currentUser?.accessToken
            if (accessToken != null) {
                val authHeader = FetLifeService.AUTH_HEADER_PREFIX + accessToken
                put("Authorization", authHeader)
            }
            put("X-Fetlife-Webview", "1")
            put("X-Fetlife-Android", VersionUtil.getCurrentVersionInt().toString())
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


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        val url = web_view?.url ?: getStringArgument(ARG_PAGE_URL)
        val optionsMenuWebNavigation = FetLifeApplication.getInstance().webAppNavigation.getOptionsMenuNavigationList(url) ?: return
        var order = 0
        for (navigationItem in optionsMenuWebNavigation) {
            menu?.add(0,navigationItem,order++,navigationItem)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
        val url = FetLifeApplication.getInstance().webAppNavigation.getOptionMenuNavigationUrl(item.itemId)
        val navigated = FetLifeApplication.getInstance().webAppNavigation.navigate(Uri.parse(url), web_view, activity as BaseActivity)
        if (!navigated) {
            web_view?.loadUrl(url,createRequestHeaders())
            web_view?.postDelayed(Runnable{activity?.invalidateOptionsMenu()},100)
        }
        return true
    }


    fun getFabLink(): String? {
        //TODO(WEBAPP): implement to be more dynamic
        return FetLifeApplication.getInstance().webAppNavigation.getFabLink(getStringArgument(ARG_PAGE_URL))
    }

    fun getCurrentUrl(): String? {
        return web_view?.url
    }

}