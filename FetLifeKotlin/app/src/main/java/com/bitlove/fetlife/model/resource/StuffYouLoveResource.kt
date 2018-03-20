package com.bitlove.fetlife.model.resource

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication

//class StuffYouLoveResource(forceLoad: Boolean, page: Int, limit: Int) : SyncResource<List<ExploreStory>>(forceLoad) {
//
//    private val stuffYouLoveDao = FetLifeApplication.instance.fetLifeDatabase.exploreDao()
//
//    val page = page
//    val limit = limit
//
//    override fun loadFromDb(): LiveData<List<ExploreStory>> {
//        return stuffYouLoveDao.getAll()
//    }
//
//    override fun shouldSync(data: List<ExploreStory>?, forceSync: Boolean): Boolean {
//        //TODO : Consider using expiration time
//        return forceSync
//    }
//
//    override fun syncWithNetwork(data: List<ExploreStory>?) {
//        FetLifeApplication.instance.jobManager.addJobInBackground(GetStuffYouLoveJob())
//    }
//}