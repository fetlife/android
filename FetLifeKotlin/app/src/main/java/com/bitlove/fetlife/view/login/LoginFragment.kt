package com.bitlove.fetlife.view.login

import android.app.Fragment
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.closeKeyboard
import com.bitlove.fetlife.databinding.FragmentLoginBinding
import com.bitlove.fetlife.logic.viewmodel.LoginViewModel
import com.bitlove.fetlife.model.dataobject.wrapper.User
import com.bitlove.fetlife.view.dialog.InformationDialog
import com.bitlove.fetlife.view.generic.BindingFragment
import com.bitlove.fetlife.view.navigation.NavigationCallback
import com.bitlove.fetlife.view.navigation.PhoneNavigationActivity
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast

class LoginFragment : BindingFragment<FragmentLoginBinding, LoginViewModel>() {

    private var navigationCallBack: NavigationCallback? = null

    override fun getViewModelClass(): Class<LoginViewModel>? {
        return LoginViewModel::class.java
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_login
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        navigationCallBack = context as? NavigationCallback
    }

    override fun onDetach() {
        super.onDetach()
        navigationCallBack = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel == null) {
            return
        }

        if (savedInstanceState == null) {
            InformationDialog.show(activity!!,InformationDialog.InfoType.ALPHA_VERSION,InformationDialog.InfoType.ALPHA_VERSION.toString(),true)
        }

        login_button.setOnClickListener { view ->
            val timeStamp = System.currentTimeMillis()
            val username = username_field.text.toString()
            viewModel!!.login(username,password_field.text.toString(), login_remember.isChecked, this, {
                progressTracker ->
                if (!isAdded) {
                    return@login
                }
                binding.progressTracker = progressTracker
                if (progressTracker?.isFailed() == true) {
                    //TODO: use appropriate error display
                    toast(R.string.toast_login_failed)
                } else if (progressTracker?.isFinished() == true) {
                    closeKeyboard()
                    PhoneNavigationActivity.start(view.context)
                }
            })
        }
        login_recover_link.setOnClickListener { view -> navigationCallBack?.onNavigate(R.id.navigation_recover) }
        login_signup_link.setOnClickListener { view -> navigationCallBack?.onNavigate(R.id.navigation_signup) }
    }
}