package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.util.Log
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreStoryEntity
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.resource.BaseResource
import com.bitlove.fetlife.model.resource.ResourceResult
import org.jetbrains.anko.coroutines.experimental.bg

abstract class GetListResource<ResourceType>(userId : String?, val pageSize: Int = 15) : BaseResource<PagedList<ResourceType>>(userId) {

    private var pageSynced: Int = 0

    override fun execute() : ResourceResult<PagedList<ResourceType>> {
        pageSynced = 0
        loadInBackground()
        return super.execute()
    }

    open fun loadInBackground() {
        bg {
            getContentDatabaseWrapper().safeRun(userId, {
                contentDb ->
                val dbSource = initLoad(contentDb)
                loadResult.liveData.addSource(dbSource, {data ->
                    if (pageSynced == 1) {
                        syncWithNetwork(2, getItemAtPosition(pageSize-1))
                        pageSynced = 2

                    }
//                    pagedListCallback.data = data
//                    data?.addWeakCallback(null,pagedListCallback)

//                    Log.e("*****DBDATA*****",(this@GetListResource as? GetExploreListResource)?.type?.toString()?:"no")
//                    for (entity in data!!) {
//                        Log.e("*",(entity as? ExploreStory)?.getEntity()?.serverOrder?.toString()?:"-1")
//                    }

                    loadResult.liveData.value = data
                })
            })
        }
    }

    override fun loadMore(): ResourceResult<PagedList<ResourceType>> {
        syncWithNetwork(pageSynced+1, getItemAtPosition((pageSynced * pageSize) -1))
        pageSynced++
        return super.loadMore()
    }

    open fun getItemAtPosition(position: Int) : ResourceType? {
        return loadResult.liveData.value?.getOrNull(position)
    }

    private fun initLoad(contentDb: FetLifeContentDatabase) : LiveData<PagedList<ResourceType>> {
        syncWithNetwork(1, null)
        pageSynced = 1
        return LivePagedListBuilder<Int,ResourceType>(loadListFromDb(contentDb),PagedList.Config.Builder().setPageSize(pageSize).setPrefetchDistance(pageSize).setInitialLoadSizeHint(pageSize).setEnablePlaceholders(true).build()).build()
    }

    abstract fun syncWithNetwork(page: Int?, item: ResourceType?)

    abstract fun loadListFromDb(contentDb: FetLifeContentDatabase) : DataSource.Factory<Int,ResourceType>
}