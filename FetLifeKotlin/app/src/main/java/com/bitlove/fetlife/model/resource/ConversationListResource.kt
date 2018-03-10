package com.bitlove.fetlife.model.resource

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.joined.ConversationWithMessages
import com.bitlove.fetlife.model.network.job.getresource.GetConversationListJob

class ConversationListResource(forceLoad: Boolean, page: Int, limit: Int) : SyncResource<List<ConversationWithMessages>>(forceLoad) {

    private val conversationWithMessagesDao = FetLifeApplication.instance.fetlifeDatabase.conversationWithMessagesDao()

    val page = page
    val limit = limit

    override fun loadFromDb(): LiveData<List<ConversationWithMessages>> {
        return conversationWithMessagesDao.getAllWithMessages()
    }

    override fun shouldSync(data: List<ConversationWithMessages>?, forceSync: Boolean): Boolean {
        //TODO : Consider using expiration time
        return forceSync
    }

    override fun syncWithNetwork(data: List<ConversationWithMessages>?) {
        FetLifeApplication.instance.jobManager.addJobInBackground(GetConversationListJob())
    }
}