package com.bitlove.fetlife.webapp.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.model.api.FetLifeService
import com.bitlove.fetlife.webapp.communication.WebViewInterface
import com.bitlove.fetlife.webapp.kotlin.getStringArgument
import com.bitlove.fetlife.webapp.navigation.FetLifeWebViewClient
import kotlinx.android.synthetic.main.webapp_fragment_webview.*
import kotlinx.android.synthetic.main.webapp_fragment_webview.view.*


class FetLifeWebViewFragment : Fragment() {
    
    companion object {
        private const val ARG_PAGE_URL = "ARG_PAGE_URL"

        fun newInstance(pageUrl: String) : FetLifeWebViewFragment {
            return FetLifeWebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PAGE_URL,pageUrl)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.webapp_fragment_webview,container,false).apply {
            web_view.settings.javaScriptEnabled = true
            web_view.addJavascriptInterface(WebViewInterface(context), "Android")
            web_view.webViewClient = FetLifeWebViewClient()
            //TODO(WEBAPP): set title / move to navigation, remove as a parameter

            val headers = HashMap<String,String>()
            val accessToken = FetLifeApplication.getInstance().userSessionManager.currentUser.accessToken
            val authHeader = FetLifeService.AUTH_HEADER_PREFIX + accessToken
            headers.put("X-Fetlife-Webview", "1")
            headers.put("Authorization", authHeader)
            web_view.loadUrl(getStringArgument(ARG_PAGE_URL),headers)
        }
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