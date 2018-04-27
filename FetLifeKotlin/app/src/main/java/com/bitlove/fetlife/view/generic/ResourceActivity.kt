package com.bitlove.fetlife.view.generic

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bitlove.fetlife.FetLifeApplication
import com.mikepenz.iconics.context.IconicsContextWrapper

abstract class ResourceActivity : AppCompatActivity() {

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FetLifeApplication.instance.loggedInUser == null) {
            StartActivity.start(this)
        } else {
            onResourceCreate(savedInstanceState)
        }
    }

    final override fun onStart() {
        super.onStart()
        if (FetLifeApplication.instance.loggedInUser == null) {
            StartActivity.start(this)
        } else {
            onResourceStart()
        }
    }

    open fun onResourceCreate(savedInstanceState: Bundle?) {}

    open fun onResourceStart() {}

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase))
    }

}