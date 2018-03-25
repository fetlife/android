package com.bitlove.fetlife.view.navigation

import android.app.Fragment
import android.app.FragmentManager
import com.bitlove.fetlife.R
import com.bitlove.fetlife.view.conversation.ConversationsFragment
import com.bitlove.fetlife.view.explore.ExploreFragment

class NavigationFragmentFactory {

    companion object {
        const val DEFAULT_NAVIGATION = R.id.menu_item_navigation_conversations
    }

    fun createFragment(navigation: Int?, layout: NavigationCallback.Layout? = null): Fragment =
            NavigationContentFragment.newFragment(navigation ?: DEFAULT_NAVIGATION,layout)

    fun createNavigationFragmentAdapter(fragmentManager: FragmentManager, navigation: Int?, layout: NavigationCallback.Layout? = null) : NavigationPagerAdapter {
        return when (navigation) {
            R.id.menu_item_navigation_explore -> NavigationPagerAdapter(fragmentManager, layout, R.id.menu_item_navigation_explore_friends_activity, R.id.menu_item_navigation_explore_fresh_and_pervy, R.id.menu_item_navigation_explore_kinky_and_popular, R.id.menu_item_navigation_explore_stuff_you_love)
            R.id.menu_item_navigation_conversations -> NavigationPagerAdapter(fragmentManager, layout, R.id.menu_item_navigation_conversations_active, R.id.menu_item_navigation_conversations_archived)
            else -> NavigationPagerAdapter(fragmentManager, layout, R.id.menu_item_navigation_conversations_active, R.id.menu_item_navigation_conversations_archived)
        }
    }

    fun createNavigationFragment(navigation: Int?, layout: NavigationCallback.Layout? = null) : Fragment {
        return when (navigation) {
            R.id.menu_item_navigation_explore_friends_activity -> ExploreFragment()
            R.id.menu_item_navigation_explore_fresh_and_pervy -> ExploreFragment()
            R.id.menu_item_navigation_explore_kinky_and_popular -> ExploreFragment()
            R.id.menu_item_navigation_explore_stuff_you_love -> ExploreFragment()
            R.id.menu_item_navigation_conversations_active -> ConversationsFragment()
            R.id.menu_item_navigation_conversations_archived -> ConversationsFragment()
            else -> ConversationsFragment()
        }
    }

    fun getNavigationTitle(navigation: Int?) : String {
        return when (navigation) {
        //TODO use string resources
            R.id.menu_item_navigation_explore -> "ExploreStory"
            R.id.menu_item_navigation_explore_friends_activity -> "Friends"
            R.id.menu_item_navigation_explore_fresh_and_pervy -> "Fresh and Pervy"
            R.id.menu_item_navigation_explore_kinky_and_popular -> "Kinky and Popular"
            R.id.menu_item_navigation_explore_stuff_you_love -> "Stuff you Love"
            R.id.menu_item_navigation_conversations -> "Messages"
            null -> "Messages"
            R.id.menu_item_navigation_conversations_active -> "Active"
            R.id.menu_item_navigation_conversations_archived -> "Archived"
            else -> "FetLife"
        }
    }

}
