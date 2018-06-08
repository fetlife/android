package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreEvent
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.get.GetReactionListJob

class GetExploreEventResource(private val storyId: String, forceLoad: Boolean, userId: String? = getLoggedInUser()?.getLocalId()) : GetResource<ExploreEvent>(forceLoad, userId) {

    override fun loadFromDb(contentDb: FetLifeContentDatabase): LiveData<ExploreEvent> {
        return contentDb.exploreEventDao().getEvent(storyId)
    }

    override fun shouldSync(data: ExploreEvent?, forceSync: Boolean): Boolean {
        return false
    }

    override fun syncWithNetwork(data: ExploreEvent?) {
        //TODO : card detail call; get type from db
    }
}