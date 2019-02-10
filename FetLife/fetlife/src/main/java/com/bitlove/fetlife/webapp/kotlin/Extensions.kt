package com.bitlove.fetlife.webapp.kotlin

import android.app.Activity

//Activity functions
fun Activity.getStringExtra(name: String) : String? {
    return intent.extras.getString(name)
}

fun Activity.getIntExtra(name: String) : Int? {
    return intent.extras.getInt(name)
}

fun Activity.getBooleanExtra(name: String) : Boolean? {
    return intent.extras.getBoolean(name)
}