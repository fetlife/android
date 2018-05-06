package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLivePagesList
import com.bitlove.fetlife.getLoggedInUserId
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.get.GetExploreListJob
import com.bitlove.fetlife.model.network.job.get.GetReactionListJob

class GetExploreListResource(val type: ExploreStory.TYPE, val forceLoad: Boolean, val limit: Int, userId : String? = getLoggedInUserId()) : GetListResource<ExploreStory>(userId, limit) {

    override fun loadListFromDb(contentDb: FetLifeContentDatabase): DataSource.Factory<Int, ExploreStory> {
        return contentDb.exploreStoryDao().getStories(type.toString())
    }

    override fun shouldSyncWithNetwork(itemAtEnd: ExploreStory?, itemAndReached: Int): Boolean {
        return forceLoad || itemAtEnd == null
    }

    override fun syncWithNetwork(itemAtEnd: ExploreStory?, itemAndReached: Int) {
        val job = GetExploreListJob(type,limit,itemAndReached+1,null,userId)
        setProgressTracker(job.progressTrackerLiveData)
        FetLifeApplication.instance.jobManager.addJobInBackground(job)
    }
}