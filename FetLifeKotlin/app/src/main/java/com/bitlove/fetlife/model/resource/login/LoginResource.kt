package com.bitlove.fetlife.model.resource.login

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.wrapper.User
import com.bitlove.fetlife.model.network.job.login.LoginJob
import com.bitlove.fetlife.model.resource.BaseResource
import com.bitlove.fetlife.model.resource.ResourceResult
import org.jetbrains.anko.coroutines.experimental.bg

class LoginResource(private val username: String, private var password: String, private val rememberUser: Boolean) : BaseResource<List<User>>(null) {

    override fun execute() : ResourceResult<List<User>> {
        loginInBackground()
        return super.execute()
    }

    private fun loginInBackground() {
        bg {
            val loginLiveData = FetLifeApplication.instance.fetLifeUserDatabase.userDao().getLastLoggedInUser()
            loadResult.liveData.addSource(loginLiveData, {data -> loadResult.liveData.value = data})
            addJob(LoginJob(username, password, rememberUser))
            password = "0000000000000000"
        }
    }

}