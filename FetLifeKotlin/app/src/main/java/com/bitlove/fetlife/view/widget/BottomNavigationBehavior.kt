package com.bitlove.fetlife.view.widget

import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewCompat
import android.support.design.widget.CoordinatorLayout
import android.view.View


class BottomNavigationBehavior : CoordinatorLayout.Behavior<BottomNavigationView>() {

    private var height: Int = 0

    override fun onLayoutChild(parent: CoordinatorLayout?, child: BottomNavigationView?, layoutDirection: Int): Boolean {
        height = child!!.height
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout,
                                     child: BottomNavigationView,
                                     directTargetChild: View, target: View,
                                     axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: BottomNavigationView,
                                target: View, dxConsumed: Int, dyConsumed: Int,
                                dxUnconsumed: Int, dyUnconsumed: Int,
                                @ViewCompat.NestedScrollType type: Int) {
        if (dyConsumed > 0) {
            slideDown(child)
        } else if (dyConsumed < 0) {
            slideUp(child)
        }
    }

    private fun slideUp(child: BottomNavigationView) {
        child.clearAnimation()
        child.animate().translationY(0f).duration = 200
    }

    private fun slideDown(child: BottomNavigationView) {
        child.clearAnimation()
        child.animate().translationY(height.toFloat()).duration = 200
    }
}