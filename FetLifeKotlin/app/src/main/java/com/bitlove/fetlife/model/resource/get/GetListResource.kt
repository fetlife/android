package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.bitlove.fetlife.getLivePagesList
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.resource.BaseResource
import com.bitlove.fetlife.model.resource.ResourceResult
import org.jetbrains.anko.coroutines.experimental.bg

abstract class GetListResource<ResourceType>(userId : String?, val pageSize: Int = 15) : BaseResource<PagedList<ResourceType>>(userId) {

    override fun execute() : ResourceResult<PagedList<ResourceType>> {
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
                })
            })
        }
    }

    private fun loadFromDb(contentDb: FetLifeContentDatabase) : LiveData<PagedList<ResourceType>> {
        return getLivePagesList(loadListFromDb(contentDb),pageSize,object: PagedList.BoundaryCallback<ResourceType>() {
            var itemEndReached = 0
            var initialPageSynced = false
            override fun onItemAtFrontLoaded(itemAtFront: ResourceType) {
                if (!initialPageSynced && shouldSyncWithNetwork(itemAtFront,itemEndReached)) {
                    syncWithNetwork(itemAtFront, itemEndReached)
                }
                initialPageSynced = true
            }
            //TODO: handle no item loaded, and handle
            override fun onItemAtEndLoaded(itemAtEnd: ResourceType) {
                itemEndReached++
                if (shouldSyncWithNetwork(itemAtEnd, itemEndReached)) {
                    syncWithNetwork(itemAtEnd, itemEndReached)
                }
            }
            override fun onZeroItemsLoaded() {
                if (shouldSyncWithNetwork(null, itemEndReached)) {
                    syncWithNetwork(null, itemEndReached)
                }
                initialPageSynced = true
            }
        })
    }

    abstract fun syncWithNetwork(itemAtEnd: ResourceType?, itemAndReached: Int)

    abstract fun shouldSyncWithNetwork(itemAtEnd: ResourceType?, itemAndReached: Int): Boolean

    abstract fun loadListFromDb(contentDb: FetLifeContentDatabase) : DataSource.Factory<Int,ResourceType>
}