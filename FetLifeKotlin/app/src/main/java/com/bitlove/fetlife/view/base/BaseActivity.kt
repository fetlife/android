package com.bitlove.fetlife.view.base

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.mikepenz.iconics.context.IconicsContextWrapper

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase))
    }
}