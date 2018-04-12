package com.bitlove.fetlife.model.resource.put

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker
import com.bitlove.fetlife.model.resource.BaseResource
import org.jetbrains.anko.coroutines.experimental.bg

abstract class PutResource<ResourceType> : BaseResource<ResourceType>() {

    override fun put(resource: ResourceType) : LiveData<ProgressTracker> {
        putInBackground(resource)
        return super.put(resource)
    }

    private fun putInBackground(resource: ResourceType) {
        bg {
            saveToDb(resource)
            if (shouldSync(resource)) {
                syncWithNetwork(resource)
            }
        }
    }

    abstract fun saveToDb(resource: ResourceType)

    abstract fun shouldSync(data: ResourceType): Boolean

    abstract fun syncWithNetwork(data: ResourceType)

}