package com.bitlove.fetlife.view.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.Menu
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil
import kotlinx.android.synthetic.main.activity_phone_navigation.*
import kotlinx.android.synthetic.main.include_appbar.*
import com.bitlove.fetlife.view.widget.BottomNavigationBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import com.bitlove.fetlife.*
import com.bitlove.fetlife.view.login.LoginActivity
import com.bitlove.fetlife.view.widget.FloatingActionButtonBehavior
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.wrapper.Group
import com.bitlove.fetlife.view.generic.ResourceActivity
import com.bitlove.fetlife.view.widget.BottomNavigationSeparatorBehavior
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import org.jetbrains.anko.coroutines.experimental.bg

//TODO: move away navigation callback from being a context object
open class PhoneNavigationActivity : ResourceActivity(), NavigationCallback {

    companion object {
        const val STATE_KEY_NAVIGATION = "STATE_KEY_NAVIGATION"
        const val STATE_KEY_LAYOUT = "STATE_KEY_LAYOUT"

        fun start(context: Context) {
            val intent = Intent(context, PhoneNavigationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
            context.startActivity(intent)
        }
    }

    open var navigation: Int? = null
    open var layout: NavigationCallback.Layout? = null

    open val navigationFragmentFactory = FetLifeApplication.instance.navigationFragmentFactory
    open var savedInstanceState : Bundle? = null

    override fun onResourceCreate(savedInstanceState: Bundle?) {
        this.savedInstanceState = savedInstanceState
        setLayoutResource()
        setStateContentFragment(savedInstanceState)
        setTitle()
        setActionBar()
        setBottomNavigation()
        setSideNavigation()
        setFloatingActionButton()
    }

    override fun onOpenUrl(url: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNavigate(navigation: Int?) : Boolean {
        this.navigation = navigation
        setContentFragment(navigationFragmentFactory.createFragment(navigation,layout))
        setTitle()
        return true
    }

    override fun onChangeView(navigation: Int?) {
        this.navigation = navigation
        setTitle()
    }

    override fun onCardNavigate(cardList: List<CardViewDataHolder>, position: Int, screenTitle: String?, scrollToBottom: Boolean) {
        val innerCard = cardList[position].getChild()
        when(innerCard) {
            //TODO: make disucssion list also swipeable
            is Group -> PhoneCardListActivity.start(this, innerCard)
            else -> PhoneCardActivity.start(this, cardList, position, screenTitle, scrollToBottom)
        }
    }

    override fun onLayoutChange(layout: NavigationCallback.Layout?) {
        this.layout = layout
        val contentFragment = supportFragmentManager.findFragmentById(R.id.content_fragment_container)
        var currentNavigation : Int? = null
        if (contentFragment!= null && contentFragment is NavigationContentFragment) {
            currentNavigation = contentFragment.getCurrentNavigation()
        }
        setContentFragment(navigationFragmentFactory.createFragment(navigation,layout,currentNavigation),false)
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

    open fun setStateContentFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            navigation = savedInstanceState.getInt(STATE_KEY_NAVIGATION)
            layout = savedInstanceState.getSerializable(STATE_KEY_LAYOUT) as? NavigationCallback.Layout
        } else {
            setContentFragment(createFragment(),false)
        }
    }

    open fun createFragment(): Fragment {
        return navigationFragmentFactory.createFragment(navigation)
    }

    open fun setContentFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.inTransaction {
            val transaction = replace(R.id.content_fragment_container, fragment)
            if (addToBackStack) {
                transaction.addToBackStack(null)
            } else {
                transaction
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    open fun setTitle() {
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

        supportActionBar?.setHomeAsUpIndicator(IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_menu).color(getSafeColor(R.color.toolbar_icon_color)).sizeDp(18))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigation_view.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.navigation_logout) {
                //TODO: ask about notification registration
                //TODO: ask about clear local content
                FetLifeApplication.instance.onUserLoggedOut()
                LoginActivity.start(this)
                true
            } else if (menuItem.itemId == R.id.navigation_reset) {
                bg {
                    FetLifeApplication.instance.fetLifeContentDatabaseWrapper.safeRun(getLoggedInUser()?.getLocalId(),{
                        contentDb ->
                        contentDb.memberDao().deleteAll()
                        contentDb.exploreStoryDao().deleteAll()
                        contentDb.jobProgressDao().deleteAll()
                    })
                    FetLifeApplication.instance.fetLifeUserDatabase.jobProgressDao().deleteAll()
                    FetLifeApplication.instance.fetLifeUserDatabase.userDao().delete(getLoggedInUser()?.getLocalId()!!)
                }
                //TODO: ask about notification registration
                //TODO: ask about clear local content
                FetLifeApplication.instance.onUserLoggedOut()
                LoginActivity.start(this)
                true
            }
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
        navigation_bottom.selectedItemId = R.id.navigation_explore

        //TODO: merge layout behaviours together
        val navLayoutParams = navigation_bottom.layoutParams as CoordinatorLayout.LayoutParams
        navLayoutParams.behavior = BottomNavigationBehavior()
        val navSepLayoutParams = navigation_bottom_separator.layoutParams as CoordinatorLayout.LayoutParams
        navSepLayoutParams.behavior = BottomNavigationSeparatorBehavior()


        //TODO move this to a better place considering tablet navigation
        navigation_bottom.setOnNavigationItemSelectedListener({
            item -> onNavigate(item.itemId)
        })
    }

    open fun setFloatingActionButton() {
        val layoutParams = button_floating.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.behavior = FloatingActionButtonBehavior()
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

    open fun inflateIconicsMenu(menuRes: Int, menu: Menu?) {
        IconicsMenuInflaterUtil.inflate(menuInflater, this@PhoneNavigationActivity, menuRes, menu)
    }
}