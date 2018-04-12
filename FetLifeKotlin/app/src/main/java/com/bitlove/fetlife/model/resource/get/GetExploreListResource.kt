package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.network.job.get.GetExploreListJob
import com.bitlove.fetlife.model.network.job.get.GetReactionListJob

class GetExploreListResource(val type: ExploreStory.TYPE, forceLoad: Boolean, val page: Int, val limit: Int, val marker: String? = null) : GetResource<List<ExploreStory>>(forceLoad) {

    private val exploreStoryDao = FetLifeApplication.instance.fetLifeContentDatabase.exploreStoryDao()

    override fun loadFromDb(): LiveData<List<ExploreStory>> {
        //TODO add non supporter limit
        return exploreStoryDao.getStories(type.toString())
    }

    override fun shouldSync(data: List<ExploreStory>?, forceSync: Boolean): Boolean {
        //TODO : Consider using expiration time
        return forceSync
    }

    override fun syncWithNetwork(data: List<ExploreStory>?) {
        val job = GetExploreListJob(type,limit,page,marker)
        setProgressTracker(job.progressTrackerLiveData)
        FetLifeApplication.instance.jobManager.addJobInBackground(job)
    }

}