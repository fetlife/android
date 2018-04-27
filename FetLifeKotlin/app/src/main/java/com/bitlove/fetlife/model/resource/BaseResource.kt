package com.bitlove.fetlife.model.resource

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUserId
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker
import com.bitlove.fetlife.model.db.FetLifeContentDatabaseWrapper

abstract class BaseResource<ResourceType>(val userId : String?) {

    open var loadResult = object : ResourceResult<ResourceType>(){
        override fun execute(): ResourceResult<ResourceType> {
            return this@BaseResource.execute()
        }
    }

    open fun execute() : ResourceResult<ResourceType> { return loadResult}

    open fun setProgressTracker(progressTracker: LiveData<ProgressTracker>) {
        loadResult.progressTracker.addSource(progressTracker, {data -> loadResult.progressTracker.value = data})
    }

    fun getContentDatabaseWrapper() : FetLifeContentDatabaseWrapper {
        return FetLifeApplication.instance.fetLifeContentDatabaseWrapper
    }

}