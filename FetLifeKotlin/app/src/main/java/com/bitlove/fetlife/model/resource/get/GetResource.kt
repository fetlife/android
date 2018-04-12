package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker
import com.bitlove.fetlife.model.resource.BaseResource
import com.bitlove.fetlife.model.resource.ResourceResult
import org.jetbrains.anko.coroutines.experimental.bg

abstract class GetResource<ResourceType>(forceSync : Boolean) : BaseResource<ResourceType>() {

    private val forceSync = forceSync
    private var synced = false

    override fun load() : ResourceResult<ResourceType> {
        loadInBackground()
        return super.load()
    }

    private fun loadInBackground() {
        bg {
            val dbSource = loadFromDb()
            loadResult.liveData.addSource(dbSource, {data ->
                loadResult.liveData.value = data
                //TODO(cleanup) implement with should sync
                if (!synced) {
                    synced = true
                    syncWithNetwork(data)
                }
            })
        }
    }

    abstract fun loadFromDb() : LiveData<ResourceType>

    abstract fun shouldSync(data: ResourceType?, forceSync: Boolean): Boolean

    abstract fun syncWithNetwork(data: ResourceType?)

    //TODO check this out about merging live data sources: https://proandroiddev.com/android-room-handling-relations-using-livedata-2d892e40bd53
}