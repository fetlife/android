package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.network.job.get.GetCommentListJob
import com.bitlove.fetlife.model.network.job.get.GetMessageListJob

class CommentListResource(val parent: SyncObject<*>, forceLoad: Boolean, page: Int, limit: Int) : GetResource<List<Reaction>>(forceLoad) {

    private val reactionDao = FetLifeApplication.instance.fetLifeContentDatabase.reactionDao()

    val page = page
    val limit = limit

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
        if (content?.getEntity()?.type == Content.TYPE.CONVERSATION.toString()) {
            FetLifeApplication.instance.jobManager.addJobInBackground(GetMessageListJob(content))
        } else {
            FetLifeApplication.instance.jobManager.addJobInBackground(GetCommentListJob(content))
        }
    }
}