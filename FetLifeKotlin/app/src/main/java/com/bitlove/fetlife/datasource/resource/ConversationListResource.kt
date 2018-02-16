package com.bitlove.fetlife.datasource.resource

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.datasource.dataobject.Conversation

class ConversationListResource(forceLoad: Boolean, page: Int, limit: Int) : SyncResource<List<Conversation>>(forceLoad) {

    val conversationDao = FetLifeApplication.instance!!.fetlifeDatabase.conversationDao()
    val page = page
    val limit = limit

    override fun loadFromDb(): LiveData<List<Conversation>> {
        return conversationDao.getAllOrderedByLastUpdated()
    }

    override fun shouldSync(data: List<Conversation>?, forceSync: Boolean): Boolean {
        page =
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun syncWithNetwork(data: List<Conversation>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}