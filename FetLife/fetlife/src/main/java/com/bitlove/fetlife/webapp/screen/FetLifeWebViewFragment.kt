package com.bitlove.fetlife.webapp.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.bitlove.fetlife.R
import com.bitlove.fetlife.webapp.communication.WebViewInterface
import com.bitlove.fetlife.webapp.kotlin.getStringArgument
import com.bitlove.fetlife.webapp.navigation.FetLifeWebViewClient
import kotlinx.android.synthetic.main.webapp_fragment_webview.view.*


class FetLifeWebViewFragment : Fragment() {
    
    companion object {
        private const val ARG_PAGE_URL = "ARG_PAGE_URL"
        private const val ARG_PAGE_TITLE = "ARG_PAGE_TITLE"

        fun newInstance(pageUrl: String, title: String?) : FetLifeWebViewFragment {
            return FetLifeWebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PAGE_URL,pageUrl)
                    putString(ARG_PAGE_TITLE,title)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.webapp_fragment_webview,container,false).apply {
            web_view.settings.javaScriptEnabled = true
            web_view.addJavascriptInterface(WebViewInterface(context), "Android")
            web_view.webViewClient = FetLifeWebViewClient()

            //TODO(WEBAPP): set title
            web_view.loadUrl(getStringArgument(ARG_PAGE_URL))
        }
    }

}