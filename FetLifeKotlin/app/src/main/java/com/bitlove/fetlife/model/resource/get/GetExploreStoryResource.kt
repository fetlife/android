package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.get.GetReactionListJob

class GetExploreStoryResource(private val storyId: String, forceLoad: Boolean, userId : String? = getLoggedInUser()?.getLocalId()) : GetResource<ExploreStory>(forceLoad, userId) {

    override fun loadFromDb(contentDb: FetLifeContentDatabase): LiveData<ExploreStory> {
        return contentDb.exploreStoryDao().getStory(storyId)
    }

    override fun shouldSync(data: ExploreStory?, forceSync: Boolean): Boolean {
        //TODO : Consider using expiration time
        return forceSync
    }

    override fun syncWithNetwork(data: ExploreStory?) {
        //TODO : card detail call; get type from db
    }
}