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

    override fun shouldSyncWithNetwork(item: ExploreStory?, frontItem: Boolean): Boolean {
        return forceLoad || item == null
    }

    override fun syncWithNetwork(item: ExploreStory?, frontItem: Boolean) {
        bg {
            var serverOrder = 0L

            if (item != null && !frontItem) {
                getContentDatabaseWrapper().safeRun(userId,{contentDb->
                    serverOrder = contentDb.exploreStoryDao().getStoryServerOrder(item!!.getLocalId()!!)
                    //TODO: remove this after proper explore item handling
                    serverOrder = serverOrder/pageSize*pageSize+pageSize-1
                })
            }
            addJob(GetExploreListJob(type,limit,(((serverOrder+1)/pageSize)+1).toInt(),item?.getCreatedAt(),userId))
        }
    }
}