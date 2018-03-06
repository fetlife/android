package com.bitlove.fetlife.view.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes

abstract class BindingActivity<DataBinding : ViewDataBinding> : BaseActivity() {

    @LayoutRes
    abstract fun getLayoutRes(): Int

    lateinit var binding : DataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutRes())
    }


}