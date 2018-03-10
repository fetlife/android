package com.bitlove.fetlife.model.resource

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.temp.ExploreStory
import com.bitlove.fetlife.model.network.job.getresource.GetStuffYouLoveJob

class StuffYouLoveResource(forceLoad: Boolean, page: Int, limit: Int) : SyncResource<List<ExploreStory>>(forceLoad) {

    private val stuffYouLoveDao = FetLifeApplication.instance.fetlifeDatabase.exploreDao()

    val page = page
    val limit = limit

    override fun loadFromDb(): LiveData<List<ExploreStory>> {
        return stuffYouLoveDao.getAll()
    }

    override fun shouldSync(data: List<ExploreStory>?, forceSync: Boolean): Boolean {
        //TODO : Consider using expiration time
        return forceSync
    }

    override fun syncWithNetwork(data: List<ExploreStory>?) {
        FetLifeApplication.instance.jobManager.addJobInBackground(GetStuffYouLoveJob())
    }
}