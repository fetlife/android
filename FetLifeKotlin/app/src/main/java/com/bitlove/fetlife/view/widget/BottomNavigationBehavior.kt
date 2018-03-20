package com.bitlove.fetlife.view.widget

import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewCompat
import android.support.design.widget.CoordinatorLayout
import android.view.View


class BottomNavigationBehavior : CoordinatorLayout.Behavior<BottomNavigationView>() {

    companion object {
        const val ANIM_SLIDE_DURATION = 5L
    }

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

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: BottomNavigationView, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        if (dy > 0) {
            slideDown(child)
        } else if (dy < 0) {
            slideUp(child)
        }
    }

    private fun slideUp(child: BottomNavigationView) {
//        child.clearAnimation()
//        val translation = child.animate().translationY(0f)
//        translation.duration = ANIM_SLIDE_DURATION
        child.translationY = 0f
    }

    private fun slideDown(child: BottomNavigationView) {
//        child.clearAnimation()
//        val translation = child.animate().translationY(height.toFloat())
//        translation.duration = ANIM_SLIDE_DURATION
        child.translationY = height.toFloat()
    }
}