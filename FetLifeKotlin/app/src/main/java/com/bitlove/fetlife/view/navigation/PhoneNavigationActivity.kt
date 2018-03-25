package com.bitlove.fetlife.view.navigation

import android.app.Fragment
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.mikepenz.iconics.context.IconicsContextWrapper
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil
import kotlinx.android.synthetic.main.activity_phone_navigation.*
import kotlinx.android.synthetic.main.include_appbar.*
import com.bitlove.fetlife.view.widget.BottomNavigationBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import com.bitlove.fetlife.*
import com.bitlove.fetlife.view.widget.FloatingActionButtonBehavior
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable

open class PhoneNavigationActivity : AppCompatActivity(), NavigationCallback {

    companion object {
        const val STATE_KEY_NAVIGATION = "STATE_KEY_NAVIGATION"
        const val STATE_KEY_LAYOUT = "STATE_KEY_LAYOUT"
    }

    private var navigation: Int? = null
    private var layout: NavigationCallback.Layout? = null

    val navigationFragmentFactory = FetLifeApplication.instance.navigationFragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutResource()
        setStateContentFragment(savedInstanceState)
        setTitle()
        setActionBar()
        setBottomNavigation()
        setSideNavigation()
        setFloatingActionButton()
    }

    override fun onNavigate(navigation: Int?) : Boolean {
        this.navigation = navigation
        setContentFragment(navigationFragmentFactory.createFragment(navigation,layout))
        setTitle()
        return true
    }

    override fun onLayoutChange(layout: NavigationCallback.Layout?) {
        this.layout = layout
        setContentFragment(navigationFragmentFactory.createFragment(navigation,layout),false)
    }

    open fun setLayoutResource() {
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

    private fun setStateContentFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            navigation = savedInstanceState.getInt(STATE_KEY_NAVIGATION)
            layout = savedInstanceState.getSerializable(STATE_KEY_LAYOUT) as? NavigationCallback.Layout
        }
        setContentFragment(navigationFragmentFactory.createFragment(navigation),false)
    }

    private fun setContentFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragmentManager.inTransaction {
            val transaction = replace(R.id.content_fragment_container, fragment)
            if (addToBackStack) {
                transaction.addToBackStack(null)
            } else {
                transaction
            }
        }
    }

    private fun setTitle() {
        super.setTitle(navigationFragmentFactory.getNavigationTitle(navigation))
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (navigation != null) {
            outState?.putInt(STATE_KEY_NAVIGATION,navigation!!)
        }
        if (layout != null) {
            outState?.putSerializable(STATE_KEY_LAYOUT,layout)
        }
    }

    open fun setSideNavigation() {

        supportActionBar?.setHomeAsUpIndicator(IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_menu).color(getSafeColor(R.color.midGray)).sizeDp(24))
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

    private fun setFloatingActionButton() {
        val layoutParams = button_change_layout.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.behavior = FloatingActionButtonBehavior()
        button_change_layout.setOnClickListener({
            view -> onLayoutChange(layout?.next())
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