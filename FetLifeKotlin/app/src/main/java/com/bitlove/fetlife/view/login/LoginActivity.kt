package com.bitlove.fetlife.view.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.inTransaction
import com.bitlove.fetlife.view.navigation.NavigationCallback
import com.bitlove.fetlife.view.navigation.PhoneNavigationActivity
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder

class LoginActivity : FragmentActivity(), NavigationCallback {
    companion object {
        const val STATE_KEY_NAVIGATION = "STATE_KEY_NAVIGATION"
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    private var navigation: Int? = null
    private val navigationFragmentFactory = FetLifeApplication.instance.navigationFragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_fragment)
        setStateContentFragment(savedInstanceState)
        setTitle()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (navigation != null) {
            outState?.putInt(PhoneNavigationActivity.STATE_KEY_NAVIGATION,navigation!!)
        }
    }

    private fun setStateContentFragment(savedInstanceState: Bundle?) {
        navigation = savedInstanceState?.getInt(LoginActivity.STATE_KEY_NAVIGATION) ?: R.id.navigation_login
        if (savedInstanceState == null) {
            setContentFragment(navigationFragmentFactory.createFragment(navigation),false)
        }
    }

    private fun setContentFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.inTransaction {
            val transaction = replace(R.id.fragment_container, fragment)
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


    override fun onNavigate(navigation: Int?) : Boolean {
        this.navigation = navigation
        setContentFragment(navigationFragmentFactory.createFragment(navigation,null))
        setTitle()
        return true
    }

    //TODO make NavigationCallBack an abstract class rather
    override fun onOpenUrl(url: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}