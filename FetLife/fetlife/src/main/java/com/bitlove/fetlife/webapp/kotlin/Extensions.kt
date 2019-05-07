package com.bitlove.fetlife.webapp.kotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bitlove.fetlife.common.app.FetLifeApplication

//Activity functions
fun Activity.getStringExtra(name: String) : String? {
    return intent?.extras?.getString(name)
}

fun Activity.getIntExtra(name: String) : Int? {
    return intent?.extras?.getInt(name)
}

fun Activity.getBooleanExtra(name: String) : Boolean? {
    return intent?.extras?.getBoolean(name)
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

fun Uri.openInBrowser() {
    this.toString().openInBrowser()
}

fun String.openInBrowser() {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.data = Uri.parse(this)
    FetLifeApplication.getInstance().startActivity(intent)
}