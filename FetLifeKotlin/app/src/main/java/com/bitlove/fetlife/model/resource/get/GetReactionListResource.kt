package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.network.job.get.GetReactionListJob

class GetReactionListResource(val type: Reaction.TYPE, val parent: SyncObject<*>, forceLoad: Boolean, val page: Int, val limit: Int, val sinceMarker: String? = null, val untilMarker: String? = null) : GetResource<List<Reaction>>(forceLoad) {

    private val reactionDao = FetLifeApplication.instance.fetLifeContentDatabase.reactionDao()

    override fun loadFromDb(): LiveData<List<Reaction>> {
        val content = (parent as? Content) ?: (parent as? ExploreStory)?.getContent() ?: null
        return reactionDao.getReactions(content?.getLocalId()!!)
    }

    override fun shouldSync(data: List<Reaction>?, forceSync: Boolean): Boolean {
        //TODO : Consider using expiration time
        return true
    }

    override fun syncWithNetwork(data: List<Reaction>?) {
        val content = (parent as? Content) ?: (parent as? ExploreStory)?.getContent() ?: return
        val job = GetReactionListJob(type,content,limit,page,sinceMarker,untilMarker)
        setProgressTracker(job.progressTrackerLiveData)
        FetLifeApplication.instance.jobManager.addJobInBackground(job)
    }
}