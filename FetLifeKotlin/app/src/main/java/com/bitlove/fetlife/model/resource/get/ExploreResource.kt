package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory

abstract class ExploreResource(forceLoad: Boolean, page: Int, limit: Int) : GetResource<List<ExploreStory>>(forceLoad) {

    private val exploreStoryDao = FetLifeApplication.instance.fetLifeContentDatabase.exploreStoryDao()
    private val exploreEventDao = FetLifeApplication.instance.fetLifeContentDatabase.exploreEventDao()

    val page = page
    val limit = limit

    override fun loadFromDb(): LiveData<List<ExploreStory>> {
        //TODO add non supporter limit
        return exploreStoryDao.getStories(getType().toString())
    }

    override fun shouldSync(data: List<ExploreStory>?, forceSync: Boolean): Boolean {
        //TODO : Consider using expiration time
        return forceSync
    }

    abstract fun getType() : ExploreStory.TYPE

}