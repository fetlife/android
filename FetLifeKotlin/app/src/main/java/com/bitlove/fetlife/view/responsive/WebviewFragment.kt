package com.bitlove.fetlife.view.responsive

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.bitlove.fetlife.R
import com.basecamp.turbolinks.TurbolinksAdapter
import com.basecamp.turbolinks.TurbolinksSession
import com.basecamp.turbolinks.TurbolinksView
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.network.FetLifeService
import com.bitlove.fetlife.view.dialog.InformationDialog
import com.bitlove.fetlife.view.navigation.NavigationFragmentFactory
import kotlinx.android.synthetic.main.fragment_turbolinks.*
import kotlinx.android.synthetic.main.item_data_card.*
import android.webkit.WebSettings
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.fragment_weview.*
import org.apache.commons.lang3.ClassUtils.getPackageName


//TODO: check this: https://stackoverflow.com/questions/24658428/swiperefreshlayout-webview-when-scroll-position-is-at-top
class WebviewFragment : Fragment() {

    val navigationFragmentFactory: NavigationFragmentFactory = FetLifeApplication.instance.navigationFragmentFactory

    private var navigationId: Int? = null

    companion object {
        private const val ARGUMENT_KEY_NAVIGATION_ID = "ARGUMENT_KEY_NAVIGATION_ID"
        fun newInstance(navigation: Int): WebviewFragment {
            val args = Bundle()
            args.putInt(ARGUMENT_KEY_NAVIGATION_ID,navigation)
            val fragment = WebviewFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationId = arguments!!.getInt(ARGUMENT_KEY_NAVIGATION_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_weview,comments_container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val headers = HashMap<String,String>()
        headers.put("Authorization",FetLifeApplication.instance.fetlifeService.authHeader!!)
        web_view.webViewClient = WebViewClient()
        enableHTML5AppCache(web_view)
        web_view.loadUrl("https://app.fetlife.com" + navigationFragmentFactory.getNavigationUrl(navigationId!!), headers)
    }

    private fun enableHTML5AppCache(webView: WebView) {
        val webSettings = webView.getSettings()
        webSettings.setJavaScriptEnabled(true)
        webSettings.setDatabaseEnabled(true)
        webSettings.setDomStorageEnabled(true)
        webSettings.setAllowFileAccess(true)
        val appCachePath = activity!!.getCacheDir().absolutePath
        webView.getSettings().setAppCachePath(appCachePath)
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT)
        webSettings.setAppCacheEnabled(true)
        webSettings.setSupportMultipleWindows(true)
    }

}