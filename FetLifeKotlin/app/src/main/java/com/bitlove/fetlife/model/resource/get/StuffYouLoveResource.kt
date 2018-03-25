package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.network.job.get.GetStuffYouLoveJob

class StuffYouLoveResource(forceLoad: Boolean, page: Int, limit: Int) : GetResource<List<ExploreStory>>(forceLoad) {

    private val exploreStoryDao = FetLifeApplication.instance.fetLifeDatabase.exploreStoryDao()
    private val exploreEventDao = FetLifeApplication.instance.fetLifeDatabase.exploreEventDao()

    val page = page
    val limit = limit

    override fun loadFromDb(): LiveData<List<ExploreStory>> {
        //TODO add non supporter limit
        return exploreStoryDao.getStories()
    }

    override fun shouldSync(data: List<ExploreStory>?, forceSync: Boolean): Boolean {
        //TODO : Consider using expiration time
        return forceSync
    }

    override fun syncWithNetwork(data: List<ExploreStory>?) {
        FetLifeApplication.instance.jobManager.addJobInBackground(GetStuffYouLoveJob())
    }
}