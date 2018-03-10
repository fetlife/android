package com.bitlove.fetlife.view.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.bitlove.fetlife.R
import com.mikepenz.iconics.context.IconicsContextWrapper
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.include_appbar.*
import com.bitlove.fetlife.view.widget.BottomNavigationBehavior
import android.support.design.widget.CoordinatorLayout
import com.bitlove.fetlife.view.conversation.ConversationsActivity
import com.bitlove.fetlife.view.explore.ExploreActivity
import com.bitlove.fetlife.view.widget.disableShiftMode


abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayout()
        setActionBar()
        setBottomNavigation()
    }

    open fun setLayout() {
        val layoutRes = getLayoutRes()
        if (layoutRes != null) {
            setContentView(getLayoutRes())
        }
    }

    @LayoutRes
    open fun getLayoutRes() : Int {
        return R.layout.activity_base
    }

    open fun setActionBar() {
        setSupportActionBar(toolbar)
    }

    open fun setBottomNavigation() {
        //TODO consider these for final design:
        // https://github.com/mikepenz/Android-Iconics
        // https://stackoverflow.com/questions/42244144/item-selected-color-in-android-bottomnavigationview
        // https://stackoverflow.com/questions/40176244/how-to-disable-bottomnavigationview-shift-mode
        inflateIconicsMenu(R.menu.menu_navigation_bottom, navigation_bottom.menu)
        navigation_bottom.disableShiftMode()
        val layoutParams = navigation_bottom.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.behavior = BottomNavigationBehavior()

        //TODO move this to a better place considering tablet navigation
        navigation_bottom.setOnNavigationItemSelectedListener({
            item -> fun navigate() : Boolean {
                when (item.itemId) {
                    R.id.menu_item_navigation_explore -> startActivity(Intent(this@BaseActivity, ExploreActivity::class.java))
                    R.id.menu_item_navigation_conversations -> startActivity(Intent(this@BaseActivity, ConversationsActivity::class.java))
                }
                return true
            }
            navigate()
        })
    }


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase))
    }

    fun inflateIconicsMenu(menuRes: Int, menu: Menu?) {
        IconicsMenuInflaterUtil.inflate(menuInflater, this@BaseActivity, menuRes, menu)
    }
}