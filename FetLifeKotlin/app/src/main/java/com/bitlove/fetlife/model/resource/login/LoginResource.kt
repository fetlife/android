package com.bitlove.fetlife.model.resource.login

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.UserEntity
import com.bitlove.fetlife.model.dataobject.wrapper.User
import com.bitlove.fetlife.model.network.job.login.LoginJob
import org.jetbrains.anko.coroutines.experimental.bg

class LoginResource {

    fun login(username: String, password: String, rememberUser: Boolean) : LiveData<List<User>> {
        val liveData = FetLifeApplication.instance.fetLifeUserDatabase.userDao().getLastLoggedInUser()
        loginInBackground(username, password, rememberUser)
        return liveData
    }

    private fun loginInBackground(username: String, password: String, rememberUser: Boolean) {
        bg {
            FetLifeApplication.instance.jobManager.addJobInBackground(LoginJob(username, password, rememberUser))
        }
    }

}