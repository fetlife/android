package com.bitlove.fetlife.view.navigation

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.view.widget.SlideControlViewPager
import com.bitlove.fetlife.view.widget.SlidingTabLayout

class NavigationContentFragment : Fragment() {

    companion object {
        private const val ARG_KEY_NAVIGATION = "ARG_KEY_NAVIGATION"
        private const val ARG_KEY_LAYOUT = "ARG_KEY_LAYOUT"
        private const val ARG_KEY_CURRENT_ITEM = "ARG_KEY_CURRENT_ITEM"

        fun newFragment(navigation: Int, layout: NavigationCallback.Layout? = null, selectedPosition: Int? = null) : NavigationContentFragment {
            val args = Bundle()
            args.putInt(ARG_KEY_NAVIGATION,navigation)
            args.putSerializable(ARG_KEY_LAYOUT,layout)
            args.putInt(ARG_KEY_CURRENT_ITEM,selectedPosition?:-1)
            val contentFragment = NavigationContentFragment()
            contentFragment.arguments = args
            return contentFragment
        }
    }

    private val navigationFragmentFactory = FetLifeApplication.instance.navigationFragmentFactory
    private var navigationCallback: NavigationCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_navigation_content,container,false)
        val navigation = arguments!!.getInt(ARG_KEY_NAVIGATION)
        val layout = arguments!!.getSerializable(ARG_KEY_LAYOUT) as? NavigationCallback.Layout

        val adapter = navigationFragmentFactory.createNavigationFragmentAdapter(fragmentManager,navigation,layout)

        val viewPager = view.findViewById<SlideControlViewPager>(R.id.content_view_pager)
        viewPager.adapter = adapter
        val selectedPosition = adapter.getPosition(arguments!!.getInt(ARG_KEY_CURRENT_ITEM))
        if (selectedPosition >= 0) {
            viewPager.currentItem = selectedPosition
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                navigationCallback?.onChangeView(adapter.getItemId(position))
            }
        })
        val tabs = view.findViewById<SlidingTabLayout>(R.id.navigation_tabs)
        //TODO move to xml
        tabs.setDividerColorResource(R.color.silver)
        tabs.setSelectedIndicatorColorResource(R.color.bostonUniversityRed)
        if (adapter.count < 2) {
            tabs.visibility = View.GONE
        } else {
            tabs.setViewPager(viewPager)
        }

        return view
    }

    fun getCurrentNavigation() : Int {
        val viewPager = view!!.findViewById<SlideControlViewPager>(R.id.content_view_pager)
        val adapter = viewPager.adapter as NavigationPagerAdapter
        return adapter.getItemId(viewPager.currentItem)

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        navigationCallback = context as? NavigationCallback
    }

    override fun onDetach() {
        super.onDetach()
        navigationCallback = null
    }

}