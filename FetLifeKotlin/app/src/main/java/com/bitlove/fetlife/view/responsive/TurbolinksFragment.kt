package com.bitlove.fetlife.view.responsive

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.bitlove.fetlife.R
import com.basecamp.turbolinks.TurbolinksAdapter
import com.basecamp.turbolinks.TurbolinksSession
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.view.navigation.NavigationFragmentFactory
import kotlinx.android.synthetic.main.fragment_turbolinks.*
import kotlinx.android.synthetic.main.item_data_card.*

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
        navigationId = arguments.getInt(ARGUMENT_KEY_NAVIGATION_ID)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_turbolinks,comments_container,false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val turbolinksSession = TurbolinksSession.getDefault(activity)
        //TODO turn off logging
        TurbolinksSession.setDebugLoggingEnabled(true)

        turbolinksSession.activity(activity)
                .adapter(this)
                .view(turbolinks_view)
                .addProgressObserver(this)
                .addPageObserver(this)
                .restoreWithCachedSnapshot(false)
                .setPullToRefreshEnabled(false)
                .visitWithAuthHeader(FetLifeApplication.instance.fetlifeService.baseUrl + navigationFragmentFactory.getNavigationUrl(navigationId!!), FetLifeApplication.instance.fetlifeService.authHeader)
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
    }

    override fun pageInvalidated() {
    }

    override fun requestFailedWithStatusCode(statusCode: Int) {
    }

    override fun visitCompleted() {
    }

    override fun visitProposedToLocationWithAction(location: String?, action: String?) {
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun shouldOverrideUrlLoading(view: WebView?, location: String?): Boolean {
        return false
    }

}