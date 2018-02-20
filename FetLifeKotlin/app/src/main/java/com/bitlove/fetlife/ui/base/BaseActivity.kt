package com.bitlove.fetlife.ui.base

import android.support.v7.app.AppCompatActivity
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes

abstract class BaseActivity<DataBinding : ViewDataBinding> : AppCompatActivity() {

    @LayoutRes
    abstract fun getLayoutRes(): Int

    lateinit var binding : DataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutRes())
    }


}