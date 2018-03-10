package com.bitlove.fetlife.view.widget

import android.databinding.adapters.CompoundButtonBindingAdapter.setChecked
import android.support.design.internal.BottomNavigationItemView
import java.lang.reflect.AccessibleObject.setAccessible
import java.lang.reflect.Array.setBoolean
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.util.Log


fun BottomNavigationView.disableShiftMode() {
    val menuView = this.getChildAt(0) as BottomNavigationMenuView
    try {
        val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
        shiftingMode.isAccessible = true
        shiftingMode.setBoolean(menuView, false)
        shiftingMode.isAccessible = false
        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i) as BottomNavigationItemView

            val shiftingMode = item.javaClass.getDeclaredField("mShiftingMode")
            shiftingMode.isAccessible = true
            shiftingMode.setBoolean(item, false)
            shiftingMode.isAccessible = false
        }
    } catch (e: NoSuchFieldException) {
        Log.e("BNVHelper", "Unable to get shift mode field", e)
    } catch (e: IllegalAccessException) {
        Log.e("BNVHelper", "Unable to change value of shift mode", e)
    }

}
