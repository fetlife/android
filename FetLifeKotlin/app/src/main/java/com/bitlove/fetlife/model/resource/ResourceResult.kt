package com.bitlove.fetlife.model.resource

import android.arch.lifecycle.MediatorLiveData
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker

class ResourceResult<ResourceType> {
    var liveData : MediatorLiveData<ResourceType> = MediatorLiveData()
    var progressTracker: MediatorLiveData<ProgressTracker> = MediatorLiveData()
}
