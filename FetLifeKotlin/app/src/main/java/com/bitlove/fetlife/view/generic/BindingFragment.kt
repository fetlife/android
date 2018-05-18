package com.bitlove.fetlife.view.generic

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitlove.fetlife.inflateBinding

abstract class BindingFragment<DataBinding : ViewDataBinding, ViewModel : android.arch.lifecycle.ViewModel> : BaseFragment<ViewModel>(), LifecycleOwner {

    private val lifecycleRegistry: Lifecycle = LifecycleRegistry(this)

    lateinit var binding : DataBinding

    @LayoutRes
    abstract fun getLayoutRes(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = inflater.inflateBinding(getLayoutRes(),container)
        return binding.root
    }

//    override fun getLifecycle(): Lifecycle {
//        return (activity as? LifecycleOwner)?.lifecycle?:lifecycleRegistry
//    }

}