package com.bitlove.fetlife.view.base

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.bitlove.fetlife.R
import com.mikepenz.iconics.context.IconicsContextWrapper
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil
import kotlinx.android.synthetic.main.activity_phone_navigation.*
import kotlinx.android.synthetic.main.include_appbar.*
import com.bitlove.fetlife.view.widget.BottomNavigationBehavior
import android.support.design.widget.CoordinatorLayout
import com.bitlove.fetlife.disableShiftMode
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import com.bitlove.fetlife.inTransaction
import com.bitlove.fetlife.view.conversation.ConversationsFragment

open class PhoneNavigationActivity : AppCompatActivity(), NavigationCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayout()
        setActionBar()
        setBottomNavigation()
        setSideNavigation()
        addContentFragment(savedInstanceState)
    }

    private fun addContentFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            fragmentManager.inTransaction { add(R.id.content_fragment_container, ConversationsFragment()) }
        } else {
            //TODO : solve preselection
        }
    }

    override fun onNavigate(actionId: Int) : Boolean {
        when (actionId) {
            R.id.menu_item_navigation_explore -> setContentFragment(ConversationsFragment())
            R.id.menu_item_navigation_conversations -> setContentFragment(ConversationsFragment())
        }
        return true
    }

    private fun setContentFragment(fragment: Fragment) {
        fragmentManager.inTransaction { replace(R.id.content_fragment_container, fragment) }
    }

    open fun setLayout() {
        val layoutRes = getLayoutRes()
        if (layoutRes != null) {
            setContentView(getLayoutRes())
        }
    }

    @LayoutRes
    open fun getLayoutRes() : Int {
        return R.layout.activity_phone_navigation
    }

    open fun setActionBar() {
        setSupportActionBar(toolbar)
    }

    open fun setSideNavigation() {
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_add)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigation_view.setNavigationItemSelectedListener { menuItem ->
            onNavigate(menuItem.itemId)
            menuItem.isChecked = true
            drawer_layout.closeDrawers()
            true
        }
    }

    open fun setBottomNavigation() {
        //TODO consider these for final design:
        // https://github.com/aurelhubert/ahbottomnavigation
        // https://github.com/ittianyu/BottomNavigationViewEx
        // https://stackoverflow.com/questions/42244144/item-selected-color-in-android-bottomnavigationview
        // https://stackoverflow.com/questions/40176244/how-to-disable-bottomnavigationview-shift-mode
        inflateIconicsMenu(R.menu.menu_navigation_bottom, navigation_bottom.menu)
        navigation_bottom.disableShiftMode()
        navigation_bottom.selectedItemId = R.id.menu_item_navigation_conversations

        val layoutParams = navigation_bottom.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.behavior = BottomNavigationBehavior()

        //TODO move this to a better place considering tablet navigation
        navigation_bottom.setOnNavigationItemSelectedListener({
            item -> onNavigate(item.itemId)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase))
    }

    open fun inflateIconicsMenu(menuRes: Int, menu: Menu?) {
        IconicsMenuInflaterUtil.inflate(menuInflater, this@PhoneNavigationActivity, menuRes, menu)
    }
}