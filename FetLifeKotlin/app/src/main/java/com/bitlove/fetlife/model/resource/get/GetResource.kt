package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.resource.BaseResource
import com.bitlove.fetlife.model.resource.ResourceResult
import org.jetbrains.anko.coroutines.experimental.bg

abstract class GetResource<ResourceType>(forceSync : Boolean, userId : String?) : BaseResource<ResourceType>(userId) {

    private val forceSync = forceSync
    private var networkSyncChecked = false

    override fun execute() : ResourceResult<ResourceType> {
        loadInBackground()
        return super.execute()
    }

    private fun loadInBackground() {
        bg {
            getContentDatabaseWrapper().safeRun(userId, {
                contentDb ->
                val dbSource = loadFromDb(contentDb)
                loadResult.liveData.addSource(dbSource, {data ->
                    loadResult.liveData.value = data
                    //TODO(cleanup) implement with should sync
                    if (!networkSyncChecked && shouldSync(data, forceSync)) {
                        networkSyncChecked = true
                        syncWithNetwork(data)
                    }
                })
            })
        }
    }

    abstract fun loadFromDb(contentDb: FetLifeContentDatabase) : LiveData<ResourceType>

    abstract fun shouldSync(data: ResourceType?, forceSync: Boolean): Boolean

    abstract fun syncWithNetwork(data: ResourceType?)

    //TODO check this out about merging live data sources: https://proandroiddev.com/android-room-handling-relations-using-livedata-2d892e40bd53
}