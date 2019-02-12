package com.bitlove.fetlife.webapp.screen

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.bitlove.fetlife.R
import com.bitlove.fetlife.webapp.kotlin.getStringExtra

class FetLifeWebViewActivity : FragmentActivity() {

    companion object {
        private const val EXTRA_PAGE_URL = "EXTRA_PAGE_URL"
        private const val EXTRA_HAS_BOTTOM_NAVIGATION = "EXTRA_HAS_BOTTOM_NAVIGATION"
        private const val EXTRA_SELECTED_BOTTOM_NAV_ITEM = "EXTRA_SELECTED_BOTTOM_NAV_ITEM"

        fun startActivity(context: Context, pageUrl: String, hasBottomNavigation: Boolean, selectedBottomNavigationItem: Int?, newTask: Boolean) {
            context.startActivity(createIntent(context, pageUrl, hasBottomNavigation, selectedBottomNavigationItem, newTask))
        }

        fun createIntent(context: Context, pageUrl: String, hasBottomNavigation: Boolean, selectedBottomNavigationItem: Int?, newTask: Boolean): Intent {
            return Intent(context, FetLifeWebViewActivity::class.java).apply {
                putExtra(EXTRA_PAGE_URL, pageUrl)
                putExtra(EXTRA_HAS_BOTTOM_NAVIGATION, hasBottomNavigation)
                putExtra(EXTRA_SELECTED_BOTTOM_NAV_ITEM, selectedBottomNavigationItem)
                flags = if (newTask) {
                    FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_NO_ANIMATION
                } else {
                    FLAG_ACTIVITY_NO_ANIMATION
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webapp_activity_webview)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.content_layout, FetLifeWebViewFragment.newInstance(getStringExtra(EXTRA_PAGE_URL)!!), "FetLifeWebViewFragment")
                    .commit()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event)
        }

        val wentBack = (supportFragmentManager.fragments.getOrNull(0) as? FetLifeWebViewFragment)?.onKeyBack()
        return if (wentBack == true) {
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }


}