package com.bitlove.fetlife.datasource.resource

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.datasource.dataobject.Conversation
import com.bitlove.fetlife.datasource.network.job.ConversationListJob
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class ConversationListResource(forceLoad: Boolean, page: Int, limit: Int) : SyncResource<List<Conversation>>(forceLoad) {

    val conversationDao = FetLifeApplication.instance.fetlifeDatabase.conversationDao()

    val page = page
    val limit = limit

    override fun loadFromDb(): LiveData<List<Conversation>> {
        return conversationDao.getAllOrderedByLastUpdated()
    }

    override fun shouldSync(data: List<Conversation>?, forceSync: Boolean): Boolean {
        //TODO : Consider using expiration time
        return forceSync
    }

    override fun syncWithNetwork(data: List<Conversation>?) {
        FetLifeApplication.instance.jobManager.addJobInBackground(ConversationListJob())
    }
}