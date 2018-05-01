package com.bitlove.fetlife.logic.viewmodel

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker

class LoginViewModel : ViewModel() {

    fun login(username: String, password: String, rememberUser: Boolean, owner: LifecycleOwner, observer: (ProgressTracker?) -> Unit) {
        val loginResource = FetLifeApplication.instance.fetlifeDataSource.login(username,password,rememberUser)
        //TODO user observer instead of forever
        loginResource.progressTracker.observe(owner, Observer {tracker -> observer.invoke(tracker)})
        loginResource.execute()
    }

}