package com.bitlove.fetlife.view.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class SlideControlViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    var slideEnabled: Boolean = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.slideEnabled) {
            super.onTouchEvent(event)
        } else false

    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.slideEnabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, slideEnabled)
    }

}
