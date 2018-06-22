package com.bitlove.fetlife.view.navigation

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.bitlove.fetlife.R
import com.bitlove.fetlife.logic.viewmodel.CardListViewModel
import com.bitlove.fetlife.logic.viewmodel.CardListViewModelObject
import com.bitlove.fetlife.view.generic.CardListFragment
import com.bitlove.fetlife.view.generic.EmptyFragment
import com.bitlove.fetlife.view.login.LoginFragment
import com.bitlove.fetlife.view.responsive.TurbolinksFragment

class NavigationFragmentFactory {

    companion object {
        const val DEFAULT_NAVIGATION = R.id.navigation_explore
    }

    fun createFragment(navigation: Int?, layout: NavigationCallback.Layout? = null, selectedPosition: Int? = null): Fragment {
        return when(navigation) {
            R.id.navigation_login -> LoginFragment()
            R.id.navigation_logout -> LoginFragment()
            R.id.navigation_signup -> TurbolinksFragment.newInstance(R.id.navigation_signup)
            R.id.navigation_recover -> TurbolinksFragment.newInstance(R.id.navigation_recover)
            else -> NavigationContentFragment.newFragment(navigation ?: DEFAULT_NAVIGATION,layout,selectedPosition)
        }
    }

    fun createNavigationFragmentAdapter(fragmentManager: FragmentManager?, navigation: Int?, layout: NavigationCallback.Layout? = null) : NavigationPagerAdapter {
        return when (navigation) {
            R.id.navigation_explore -> NavigationPagerAdapter(fragmentManager, layout, R.id.navigation_explore_friends_activity, R.id.navigation_explore_kinky_and_popular, R.id.navigation_explore_fresh_and_pervy, R.id.navigation_explore_stuff_you_love)
            R.id.navigation_conversations -> NavigationPagerAdapter(fragmentManager, layout, R.id.navigation_conversations_inbox/*, R.id.navigation_conversations_all*/)
            R.id.navigation_favorites -> NavigationPagerAdapter(fragmentManager, layout, R.id.navigation_favorites)
            R.id.navigation_groups -> NavigationPagerAdapter(fragmentManager, layout, R.id.navigation_groups)
            R.id.navigation_notifications -> NavigationPagerAdapter(fragmentManager, NavigationCallback.Layout.WEB, R.id.navigation_notifications)
            R.id.navigation_search -> NavigationPagerAdapter(fragmentManager, NavigationCallback.Layout.WEB, R.id.navigation_search)
            //TODO rely on default constant
            else -> NavigationPagerAdapter(fragmentManager, layout, R.id.navigation_explore_friends_activity, R.id.navigation_explore_kinky_and_popular, R.id.navigation_explore_fresh_and_pervy, R.id.navigation_explore_stuff_you_love)
        }
    }

    fun createNavigationFragment(navigation: Int?, layout: NavigationCallback.Layout? = null) : Fragment {
        if (layout == NavigationCallback.Layout.WEB) {
            //TODO rely on default constant
            return TurbolinksFragment.newInstance(navigation?: R.id.navigation_conversations_inbox)
        }
        return when (navigation) {
            R.id.navigation_search -> TurbolinksFragment.newInstance(R.id.navigation_search)
            R.id.navigation_notifications -> TurbolinksFragment.newInstance(R.id.navigation_notifications)
            R.id.navigation_groups -> CardListFragment.newInstance(CardListViewModel.CardListType.GROUPS,null,getNavigationTitle(navigation))
            R.id.navigation_explore_friends_activity -> CardListFragment.newInstance(CardListViewModel.CardListType.EXPLORE_FRIENDS_FEED,null,getNavigationTitle(navigation))
            R.id.navigation_explore_fresh_and_pervy -> CardListFragment.newInstance(CardListViewModel.CardListType.EXPLORE_FRESH_AND_PERVY,null,getNavigationTitle(navigation))
            R.id.navigation_explore_kinky_and_popular -> CardListFragment.newInstance(CardListViewModel.CardListType.EXPLORE_KINKY_AND_POPULAR,null,getNavigationTitle(navigation))
            R.id.navigation_explore_stuff_you_love -> CardListFragment.newInstance(CardListViewModel.CardListType.EXPLORE_STUFF_YOU_LOVE,null,getNavigationTitle(navigation))
            R.id.navigation_conversations_inbox -> CardListFragment.newInstance(CardListViewModel.CardListType.CONVERSATIONS_INBOX,null,getNavigationTitle(navigation))
            R.id.navigation_conversations_all -> CardListFragment.newInstance(CardListViewModel.CardListType.CONVERSATIONS_ALL,null,getNavigationTitle(navigation))
            R.id.navigation_favorites -> CardListFragment.newInstance(CardListViewModel.CardListType.FAVORITES,null,getNavigationTitle(navigation))
            else -> CardListFragment.newInstance(CardListViewModel.CardListType.EXPLORE_KINKY_AND_POPULAR,null,getNavigationTitle(navigation))
        }
    }

    fun getNavigationTitle(navigation: Int?) : String {
        return when (navigation) {
        //TODO use string resources
            R.id.navigation_search -> "Search"
            R.id.navigation_favorites -> "Favorites"
            R.id.navigation_explore -> "Explore"
            R.id.navigation_groups -> "Groups"
            R.id.navigation_explore_friends_activity -> "Friends"
            R.id.navigation_explore_fresh_and_pervy -> "Fresh and Pervy"
            R.id.navigation_explore_kinky_and_popular -> "Kinky and Popular"
            R.id.navigation_explore_stuff_you_love -> "Stuff you Love"
            R.id.navigation_conversations -> "Messages"
            null -> "Friends"
            R.id.navigation_conversations_inbox -> "Inbox"
            R.id.navigation_conversations_all -> "All"
            else -> "FetLife"
        }
    }

    fun getNavigationUrl(navigationId: Int): String? {
        return when(navigationId) {
            R.id.navigation_search -> "/search"
            R.id.navigation_notifications -> "/notifications"
            R.id.navigation_recover -> "/users/password/new"
            R.id.navigation_signup -> "/signup_step_profile"
            R.id.navigation_explore_friends_activity -> "/explore/following"
            R.id.navigation_explore_fresh_and_pervy -> "/explore/fresh-and-pervy"
            R.id.navigation_explore_kinky_and_popular -> "/explore"
            R.id.navigation_explore_stuff_you_love -> "/explore/stuff-you-love"
            R.id.navigation_conversations_inbox -> "/conversations"
            R.id.navigation_conversations_all -> "/conversations/all"
            else -> "/"
        }
    }

}
