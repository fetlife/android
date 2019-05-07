package com.bitlove.fetlife.legacy.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

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
