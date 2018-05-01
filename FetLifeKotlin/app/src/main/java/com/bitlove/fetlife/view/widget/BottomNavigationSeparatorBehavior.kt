package com.bitlove.fetlife.view.widget

import android.app.Activity
import android.graphics.Rect
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewCompat
import android.support.design.widget.CoordinatorLayout
import android.view.View
import android.opengl.ETC1.getHeight




class BottomNavigationSeparatorBehavior : CoordinatorLayout.Behavior<View>() {

    override fun onMeasureChild(parent: CoordinatorLayout?, child: View?, parentWidthMeasureSpec: Int, widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int): Boolean {
        child?.visibility = if (isSoftKetBoardOpen(parent!!,parentHeightMeasureSpec)) View.INVISIBLE else View.VISIBLE
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed)
    }

    private fun isSoftKetBoardOpen(coordinatorLayout: CoordinatorLayout, parentHeightMeasureSpec: Int): Boolean {
        val activity = coordinatorLayout?.context as? Activity
        val rect = Rect()
        activity?.window?.decorView?.getWindowVisibleDisplayFrame(rect)
        val statusBarHeight = rect.top
        val screenHeight = activity?.windowManager?.defaultDisplay?.height?:0
        val diff = screenHeight - statusBarHeight - View.MeasureSpec.getSize(parentHeightMeasureSpec)
        return diff > 128
    }
}