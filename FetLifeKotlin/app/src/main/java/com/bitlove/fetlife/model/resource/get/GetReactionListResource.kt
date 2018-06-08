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

class GetReactionListResource(val type: Reaction.TYPE, val parent: SyncObject<*>, forceLoad: Boolean, val page: Int, val limit: Int, val sinceMarker: String? = null, val untilMarker: String? = null, userId : String? = getLoggedInUser()?.getLocalId()) : GetResource<List<Reaction>>(forceLoad, userId) {

    override fun loadFromDb(contentDb: FetLifeContentDatabase): LiveData<List<Reaction>> {
        val content = (parent as? Content) ?: (parent as? ExploreStory)?.getChild() as? Content ?: throw IllegalArgumentException()
        return contentDb.reactionDao().getReactions(content!!.getLocalId()!!)
    }

    override fun shouldSync(data: List<Reaction>?, forceSync: Boolean): Boolean {
        //TODO : Consider using expiration time
        return forceSync
    }

    override fun syncWithNetwork(data: List<Reaction>?) {
        val content = (parent as? Content) ?: (parent as? ExploreStory)?.getChild() as? Content ?: return
        addJob(GetReactionListJob(type,content,limit,page,sinceMarker,untilMarker,userId))
    }
}