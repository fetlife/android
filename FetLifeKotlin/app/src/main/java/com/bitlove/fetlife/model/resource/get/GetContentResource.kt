package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.network.job.get.GetReactionListJob

class GetContentResource(val contentId: String, forceLoad: Boolean) : GetResource<Content>(forceLoad) {

    private val contentDao = FetLifeApplication.instance.fetLifeContentDatabase.contentDao()

    override fun loadFromDb(): LiveData<Content> {
        return contentDao.getContent(contentId)
    }

    override fun shouldSync(data: Content?, forceSync: Boolean): Boolean {
        //TODO : Consider using expiration time
        return forceSync
    }

    override fun syncWithNetwork(data: Content?) {
        //TODO : card detail call; get type from db
    }
}