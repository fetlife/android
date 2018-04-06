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
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.model.dataobject.wrapper.User
import com.bitlove.fetlife.view.navigation.NavigationCallback
import com.bitlove.fetlife.view.navigation.PhoneNavigationActivity
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(), LifecycleOwner {

    val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle {
        return  lifecycleRegistry
    }

    private var navigationCallBack: NavigationCallback? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        navigationCallBack = context as? NavigationCallback
    }

    override fun onDetach() {
        super.onDetach()
        navigationCallBack = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_login, container, false)
        return view
    }

    fun v() {

        val userData = FetLifeApplication.instance.fetLifeUserDatabase.userDao().getLastLoggedInUser()
        userData.observeForever(object : Observer<List<User>> {
            override fun onChanged(users: List<User>?) {
                userData.removeObserver(this)
            }
        })

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO user observer instead of forever
        login_button.setOnClickListener { view ->
            val timeStamp = System.currentTimeMillis()
            val username = username_field.text.toString()
            val userData = FetLifeApplication.instance.fetlifeDataSource.login(username, password_field.text.toString(), login_remember.isChecked)
            userData.observeForever {userList ->
                val user = userList?.firstOrNull()
                if (user?.getUserName() == username && user.getLastLoggedIn() > timeStamp) {
                    PhoneNavigationActivity.start(view.context)
                }
            }
        }
        login_recover_link.setOnClickListener { view -> navigationCallBack?.onNavigate(R.id.navigation_recover) }
        login_signup_link.setOnClickListener { view -> navigationCallBack?.onNavigate(R.id.navigation_signup) }
    }
}