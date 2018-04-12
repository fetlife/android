package com.bitlove.fetlife.model.resource

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker

abstract class BaseResource<ResourceType> {

    open var loadResult = ResourceResult<ResourceType>()

    open fun load() : ResourceResult<ResourceType> { return loadResult}

    open fun put(resource: ResourceType) : LiveData<ProgressTracker> {
        return loadResult.progressTracker
    }

    open fun setProgressTracker(progressTracker: LiveData<ProgressTracker>) {
        loadResult.progressTracker.addSource(progressTracker, {data -> loadResult.progressTracker.value = data})
    }
}