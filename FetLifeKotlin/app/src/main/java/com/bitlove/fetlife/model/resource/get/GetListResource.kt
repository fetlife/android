package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import android.util.Log
import com.bitlove.fetlife.getLivePagesList
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.resource.BaseResource
import com.bitlove.fetlife.model.resource.ResourceResult
import org.jetbrains.anko.coroutines.experimental.bg

abstract class GetListResource<ResourceType>(userId : String?, val pageSize: Int = 15) : BaseResource<PagedList<ResourceType>>(userId) {

    inner class ListResourcePagingCallback : PagedList.Callback() {
        var data: PagedList<ResourceType>? = null
        private var pageRequested = 0

        override fun onChanged(position: Int, count: Int) {
            if ((position/count) >= pageRequested) {
                pageRequested = (position/count)+1
                val item = if (data == null || position <= count) null else data!![position-count-1]
                syncWithNetwork(pageRequested, item)
            }
        }

        override fun onRemoved(position: Int, count: Int) {}
        override fun onInserted(position: Int, count: Int) {
            if ((position/count) >= pageRequested) {
                pageRequested = (position/count)+1
                val item = if (data == null || position <= count) null else data!![position-count-1]
                syncWithNetwork(pageRequested, item)
            }
        }
    }

    private val pagedListCallback = ListResourcePagingCallback()

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
                    pagedListCallback.data = data
                    data?.addWeakCallback(null,pagedListCallback)
                    loadResult.liveData.value = data
                })
            })
        }
    }

    private fun loadFromDb(contentDb: FetLifeContentDatabase) : LiveData<PagedList<ResourceType>> {
        val pagedListLiveData = getLivePagesList(loadListFromDb(contentDb),pageSize,object: PagedList.BoundaryCallback<ResourceType>() {
            var initialPageSynced = false
            override fun onItemAtFrontLoaded(itemAtFront: ResourceType) {
                if (!initialPageSynced) {
                    syncWithNetwork(1, null)
                }
                initialPageSynced = true
            }
            override fun onItemAtEndLoaded(itemAtEnd: ResourceType) {
                syncWithNetwork(null, itemAtEnd)
            }
            override fun onZeroItemsLoaded() {
                if (!initialPageSynced) {
                    syncWithNetwork(1, null)
                }
                initialPageSynced = true
            }
        })
        return pagedListLiveData
    }

    abstract fun syncWithNetwork(page: Int?, item: ResourceType?)

    abstract fun loadListFromDb(contentDb: FetLifeContentDatabase) : DataSource.Factory<Int,ResourceType>
}