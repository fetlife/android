package com.bitlove.fetlife.view.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes

abstract class BindingActivity<DataBinding : ViewDataBinding> : BaseActivity() {

    lateinit var binding : DataBinding

    override fun setLayout() {
        binding = DataBindingUtil.setContentView(this, getLayoutRes())
    }


}