package com.bitlove.fetlife.view.responsive

import android.app.Fragment
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
import org.apache.commons.lang3.ClassUtils.getPackageName


//TODO: check this: https://stackoverflow.com/questions/24658428/swiperefreshlayout-webview-when-scroll-position-is-at-top
class TurbolinksFragment : Fragment(), TurbolinksAdapter, TurbolinksSession.ProgressObserver, TurbolinksSession.PageObserver {

    val navigationFragmentFactory: NavigationFragmentFactory = FetLifeApplication.instance.navigationFragmentFactory

    private var navigationId: Int? = null

    companion object {
        private const val ARGUMENT_KEY_NAVIGATION_ID = "ARGUMENT_KEY_NAVIGATION_ID"
        fun newInstance(navigation: Int): TurbolinksFragment {
            val args = Bundle()
            args.putInt(ARGUMENT_KEY_NAVIGATION_ID,navigation)
            val fragment = TurbolinksFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationId = arguments!!.getInt(ARGUMENT_KEY_NAVIGATION_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_turbolinks,comments_container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val turbolinksSession = TurbolinksSession.getDefault(activity)
        //TODO turn off logging
        TurbolinksSession.setDebugLoggingEnabled(true)

//        enableHTML5AppCache(TurbolinksSession.getDefault(context).webView)

        turbolinksSession.activity(activity)
                .adapter(this)
                .view(turbolinks_view)
                .addProgressObserver(this)
                .addPageObserver(this)
                .restoreWithCachedSnapshot(false)
                .setPullToRefreshEnabled(false)
                .visitWithAuthHeader(FetLifeService.BASE_URL + navigationFragmentFactory.getNavigationUrl(navigationId!!), FetLifeApplication.instance.fetlifeService.authHeader)

        if (savedInstanceState == null) InformationDialog.show(activity!!, InformationDialog.InfoType.WEB, navigationId.toString(), true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        TurbolinksSession.resetDefault()
    }

    override fun onDetach() {
        super.onDetach()
        TurbolinksSession.resetDefault()
    }

    override fun onPageFinished() {
    }

    override fun onReceivedError(errorCode: Int) {
        Log.e("TURBO",errorCode.toString())
    }

    override fun pageInvalidated() {
    }

    override fun requestFailedWithStatusCode(statusCode: Int) {
        Log.e("TURBO",statusCode.toString())
    }

    override fun visitCompleted() {
    }

    override fun visitProposedToLocationWithAction(location: String?, action: String?) {
        TurbolinksSession.getDefault(context)
                .activity(activity)
                .adapter(this)
                .view(turbolinks_view)
                .visitLocationWithAction(location, action)
//                .visitWithAuthHeader(location,  FetLifeApplication.instance.fetlifeService.authHeader)
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun shouldOverrideUrlLoading(view: WebView, location: String): Boolean {
        val uri = Uri.parse(location)
        if (uri.host != FetLifeService.BASE_URL) {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            return true
//        } else if (UrlUtil.handleInternal(this, uri)) {
//            return true
        }
        return false
    }

//    private fun enableHTML5AppCache(webView: WebView) {
//
//        webView.getSettings().setDomStorageEnabled(true)
//
//        // Set cache size to 8 mb by default. should be more than enough
//        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8)
//
//        // This next one is crazy. It's the DEFAULT location for your app's cache
//        // But it didn't work for me without this line
//        webView.getSettings().setAppCachePath("/data/data/" + activity!!.getPackageName() + "/cache")
//        webView.getSettings().setAllowFileAccess(true)
//        webView.getSettings().setAppCacheEnabled(true)
//
//        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT)
//    }

}