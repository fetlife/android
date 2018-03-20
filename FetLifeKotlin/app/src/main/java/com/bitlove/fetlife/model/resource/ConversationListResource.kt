package com.bitlove.fetlife.model.resource

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.network.job.getresource.GetConversationListJob

class ConversationListResource(forceLoad: Boolean, page: Int, limit: Int) : SyncResource<List<Content>>(forceLoad) {

    private val contentDao = FetLifeApplication.instance.fetLifeDatabase.contentDao()

    val page = page
    val limit = limit

    override fun loadFromDb(): LiveData<List<Content>> {
        return contentDao.getConversations()
    }

    override fun shouldSync(data: List<Content>?, forceSync: Boolean): Boolean {
        //TODO : Consider using expiration time
        return forceSync
    }

    override fun syncWithNetwork(data: List<Content>?) {
        FetLifeApplication.instance.jobManager.addJobInBackground(GetConversationListJob())
    }
}