package com.bitlove.fetlife.view.widget

import android.graphics.Rect
import android.view.TouchDelegate
import android.view.MotionEvent
import android.view.View

class TouchDelegateComposite(view: View) : TouchDelegate(emptyRect, view) {

    companion object {
        private val emptyRect = Rect()
    }

    private val delegates = ArrayList<TouchDelegate>()

    fun addDelegate(delegate: TouchDelegate?) {
        if (delegate != null) {
            delegates.add(delegate)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var res = false
        val x = event.x
        val y = event.y
        for (delegate in delegates) {
            event.setLocation(x, y)
            res = delegate.onTouchEvent(event) || res
        }
        return res
    }

}