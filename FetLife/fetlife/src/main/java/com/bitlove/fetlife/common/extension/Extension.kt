package com.bitlove.fetlife.common.extension

import android.os.Handler
import android.os.Looper

fun runOnUiThread(function: () -> Unit) {
    Handler(Looper.getMainLooper()).post { function.invoke() }
}