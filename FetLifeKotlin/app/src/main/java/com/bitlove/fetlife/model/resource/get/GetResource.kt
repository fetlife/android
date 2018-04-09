package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import org.jetbrains.anko.coroutines.experimental.bg

abstract class GetResource<ResourceType>(forceSync : Boolean) {

    private val forceSync = forceSync
    private var synced = false
    private val liveData : MediatorLiveData<ResourceType> = MediatorLiveData()

    fun load() : LiveData<ResourceType> {
        loadInBackground()
        return liveData
    }

    private fun loadInBackground() {
        bg {
            val dbSource = loadFromDb()
            liveData.addSource(dbSource, {data ->
                liveData.value = data
                //TODO implement with should sync
                if (!synced) {
                    synced = true
                    syncWithNetwork(data)
                }
            })
        }
    }

    abstract fun loadFromDb() : LiveData<ResourceType>

    //TODO check this out about merging live data sources: https://proandroiddev.com/android-room-handling-relations-using-livedata-2d892e40bd53

    abstract fun shouldSync(data: ResourceType?, forceSync: Boolean): Boolean

    abstract fun syncWithNetwork(data: ResourceType?)
}