package com.bitlove.fetlife.webapp.kotlin

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

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

fun Fragment.getStringArgument(name: String) : String? {
    return arguments?.getString(name)
}

fun Fragment.getIntArgument(name: String) : Int? {
    return arguments?.getInt(name)
}

fun Fragment.getBooleanArgument(name: String) : Boolean? {
    return arguments?.getBoolean(name)
}

fun Context.showToast(message: String) {
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
}