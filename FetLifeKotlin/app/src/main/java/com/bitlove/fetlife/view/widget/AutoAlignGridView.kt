package com.bitlove.fetlife.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridView

class AutoAlignGridView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : GridView(context, attrs, defStyleAttr, defStyleRes) {

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2,
                View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}