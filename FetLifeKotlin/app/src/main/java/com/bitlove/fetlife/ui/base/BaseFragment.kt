package com.bitlove.fetlife.ui.base

import android.app.Fragment
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitlove.fetlife.getViewModel
import com.bitlove.fetlife.inflateBinding

abstract class BaseFragment<ViewModel : android.arch.lifecycle.ViewModel> : Fragment(), LifecycleOwner {

    lateinit var viewModel : ViewModel

    abstract fun getViewModelClass() : Class<ViewModel>?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModelClass()?.getViewModel(activity as FragmentActivity) as ViewModel
    }
}