package com.bitlove.fetlife.view.generic

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.bitlove.fetlife.getViewModel

abstract class BaseFragment<ViewModel : android.arch.lifecycle.ViewModel> : Fragment(), LifecycleOwner {

    var savedInstanceState: Bundle? = null

    var viewModel : ViewModel? = null

    abstract fun getViewModelClass() : Class<ViewModel>?

    open fun getViewModelParam() : String? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        //TODO clean up
        viewModel = getViewModelClass()?.getViewModel(activity as FragmentActivity) as? ViewModel
    }
}