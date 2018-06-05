package com.bitlove.fetlife.model.resource

import android.arch.lifecycle.MediatorLiveData
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker

abstract class ResourceResult<ResourceType> {
    var liveData : MediatorLiveData<ResourceType> = MediatorLiveData()
    var progressTracker: MediatorLiveData<ProgressTracker> = MediatorLiveData()

    abstract fun execute() : ResourceResult<ResourceType>
    abstract fun loadMore() : ResourceResult<ResourceType>
    abstract fun cancel()
    abstract fun reducedPriority() : Boolean
    abstract fun normalPriority() : Boolean

}
