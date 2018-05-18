package com.bitlove.fetlife.view.widget

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async


//TODO: check this : https://github.com/yuvaraj119/Picasso-RecyclerView-StaggeredGridLayoutManager
class GridAutoFitLayoutManager : StaggeredGridLayoutManager {
    private var mColumnWidth: Int = 0
    private var mColumnWidthChanged = true

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        spanCount = 1
        setColumnWidth(checkedColumnWidth(context, mColumnWidth))
        gapStrategy = GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
    }


    private fun checkedColumnWidth(context: Context, columnWidth: Int): Int {
        var columnWidth = columnWidth
        if (columnWidth <= 0) {
            /* Set default columnWidth value (48dp here). It is better to move this constant
            to static constant on top, but we need context to convert it to dp, so can't really
            do so. */
            columnWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320f,
                    context.getResources().getDisplayMetrics()).toInt()
        }
        return columnWidth
    }

    fun setColumnWidth(newColumnWidth: Int) {
        if (newColumnWidth > 0 && newColumnWidth != mColumnWidth) {
            mColumnWidth = newColumnWidth
            mColumnWidthChanged = true
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        val width = width
        val height = height
        if (mColumnWidthChanged && mColumnWidth > 0 && width > 0 && height > 0) {
            val totalSpace = if (orientation == LinearLayoutManager.VERTICAL) {
                width - paddingRight - paddingLeft
            } else {
                height - paddingTop - paddingBottom
            }
            val spanCount = Math.max(1, totalSpace / mColumnWidth)
            async(UI) {
                setSpanCount(spanCount)
            }
            mColumnWidthChanged = false
        }
        super.onLayoutChildren(recycler, state)
    }

//    override fun requestChildRectangleOnScreen(parent: RecyclerView?, child: View?, rect: Rect?, immediate: Boolean): Boolean {
//        return false
//    }
//
//    override fun requestChildRectangleOnScreen(parent: RecyclerView?, child: View?, rect: Rect?, immediate: Boolean, focusedChildVisible: Boolean): Boolean {
//        return false
//    }
}