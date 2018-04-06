package com.bitlove.fetlife.view.generic

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.bitlove.fetlife.FetLifeApplication

abstract class ResourceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FetLifeApplication.instance.loggedInUser == null) {
            StartActivity.start(this)
        }
    }

}