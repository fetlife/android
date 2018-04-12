package com.bitlove.fetlife.view.generic

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bitlove.fetlife.FetLifeApplication
import com.mikepenz.iconics.context.IconicsContextWrapper

abstract class ResourceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FetLifeApplication.instance.loggedInUser == null) {
            StartActivity.start(this)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase))
    }

}