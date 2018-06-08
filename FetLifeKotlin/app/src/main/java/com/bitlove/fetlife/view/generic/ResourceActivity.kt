package com.bitlove.fetlife.view.generic

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.content.MemberEntity
import com.bitlove.fetlife.view.login.LoginActivity
import com.bitlove.fetlife.view.navigation.PhoneNavigationActivity
import com.mikepenz.iconics.context.IconicsContextWrapper
import org.jetbrains.anko.coroutines.experimental.bg

abstract class ResourceActivity : AppCompatActivity() {

    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    private var loginProcessStarted = false

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FetLifeApplication.instance.loggedInUser == null) {
            loginProcessStarted = true
            startLoginProcess()
        } else {
            onResourceCreate(savedInstanceState)
        }
    }

    private fun startLoginProcess() {
        bg {
            val userDao = FetLifeApplication.instance.fetLifeUserDatabase.userDao()
            userDao.clean()
            userDao.getLastLoggedInUser().observe(this, Observer{
                userList ->
                bg {
                    val user = userList?.firstOrNull()
                    if (user?.getAccessToken() != null && user.rememberUser()) {
                        val userId = user.getLocalId()
                        FetLifeApplication.instance.onUserLoggedIn(user, user.getAccessToken()!!, user.getRefreshToken())

                        var memberEntity: MemberEntity? = null
                        FetLifeApplication.instance.fetLifeContentDatabaseWrapper.safeRun(userId, {
                            contentDb ->
                            val memberDao = contentDb.memberDao()
                            memberEntity = memberDao?.getEntity(userId)
                        })
                        if (memberEntity == null) {
                            LoginActivity.start(this)
                        } else {
                            user.memberEntity = memberEntity!!
                            PhoneNavigationActivity.start(this)
                        }
                    } else {
                        LoginActivity.start(this)
                    }
                }
            })
        }
    }

    final override fun onStart() {
        super.onStart()
        when {
            loginProcessStarted -> return
            FetLifeApplication.instance.loggedInUser == null -> startLoginProcess()
            else -> onResourceStart()
        }
    }

    open fun onResourceCreate(savedInstanceState: Bundle?) {}

    open fun onResourceStart() {}

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase))
    }

}