package com.bitlove.fetlife.model.resource.get

import android.arch.paging.DataSource
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUserId
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.get.GetExploreListJob
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class GetExploreListResource(val type: ExploreStory.TYPE, val forceLoad: Boolean, val limit: Int, userId : String? = getLoggedInUserId()) : GetListResource<ExploreStory>(userId, limit) {

    override fun loadListFromDb(contentDb: FetLifeContentDatabase): DataSource.Factory<Int, ExploreStory> {
        return contentDb.exploreStoryDao().getStories(type.toString())
    }

    override fun syncWithNetwork(page: Int?, item: ExploreStory?) {
        if (forceLoad) {
            var serverOrder = item?.getEntity()?.serverOrder?:((page!!-1)*pageSize-1)
            serverOrder++

            val leftOver = serverOrder % pageSize
            if (leftOver != 0) {
                serverOrder += pageSize-leftOver
            }

            val pageToRequest = (serverOrder/limit)+1
            addJob(GetExploreListJob(type,limit,pageToRequest,item,userId))
        }
    }

    override fun getItemAtPosition(position: Int): ExploreStory? {
        val data = loadResult.liveData.value?:return null
        for (item in data.reversed()) {
            if (position >= item?.getEntity()?.serverOrder?: Int.MAX_VALUE) {
                return item
            }
        }
        return null
    }
}