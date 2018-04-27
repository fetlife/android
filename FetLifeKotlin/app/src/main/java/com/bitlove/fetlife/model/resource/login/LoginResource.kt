package com.bitlove.fetlife.model.resource.login

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker
import com.bitlove.fetlife.model.dataobject.wrapper.User
import com.bitlove.fetlife.model.network.job.login.LoginJob
import com.bitlove.fetlife.model.resource.BaseResource
import com.bitlove.fetlife.model.resource.ResourceResult
import org.jetbrains.anko.coroutines.experimental.bg

class LoginResource : BaseResource<List<User>>(null) {

    fun login(username: String, password: String, rememberUser: Boolean) : ResourceResult<List<User>> {
        loginInBackground(username, password, rememberUser)
        return loadResult
    }

    private fun loginInBackground(username: String, password: String, rememberUser: Boolean) {
        bg {
            val loginLiveData = FetLifeApplication.instance.fetLifeUserDatabase.userDao().getLastLoggedInUser()
            loadResult.liveData.addSource(loginLiveData, {data -> loadResult.liveData.value = data})
            val job = LoginJob(username, password, rememberUser)
            setProgressTracker(job.progressTrackerLiveData)
            FetLifeApplication.instance.jobManager.addJobInBackground(job)
        }
    }

}