package com.bitlove.fetlife.view.navigation

import android.app.Fragment
import android.app.FragmentManager
import android.support.v13.app.FragmentStatePagerAdapter
import com.bitlove.fetlife.FetLifeApplication

class NavigationPagerAdapter(fragmentManager: FragmentManager, val layout: NavigationCallback.Layout? = null, vararg navigationIds: Int) : FragmentStatePagerAdapter(fragmentManager) {

    private val navigationIds = navigationIds
    private val navigationFragmentFactory = FetLifeApplication.instance.navigationFragmentFactory

    override fun getCount(): Int {
        return navigationIds.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return navigationFragmentFactory.getNavigationTitle(navigationIds[position])
    }

    override fun getItem(position: Int): Fragment {
        return navigationFragmentFactory.createNavigationFragment(navigationIds[position], layout)
    }

    fun getItemId(position: Int) : Int {
        return navigationIds[position]
    }

    fun getPosition(id: Int) : Int {
        for ((i,navId) in navigationIds.withIndex()) {
            if (navId == id) {
                return i
            }
        }
        return -1
    }

}