package com.bitlove.fetlife.view.generic

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.view.login.LoginActivity
import com.bitlove.fetlife.view.navigation.PhoneNavigationActivity
import org.jetbrains.anko.coroutines.experimental.bg

class StartActivity : Activity(), LifecycleOwner {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, StartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bg {
            val userDao = FetLifeApplication.instance.fetLifeUserDatabase.userDao()
            userDao.clean()
            userDao.getLastLoggedInUser().observe(this, Observer{
                userList ->
                val user = userList?.firstOrNull()
                if (user?.getAccessToken() != null && user.rememberUser()) {
                    FetLifeApplication.instance.onUserLoggedIn(user.getUserName(), user.getAccessToken()!!, user.getRefreshToken())
                    PhoneNavigationActivity.start(this)
                } else {
                    LoginActivity.start(this)
                }
            })
        }
    }
}