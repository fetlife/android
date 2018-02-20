package com.bitlove.fetlife.datasource

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.datasource.dataobject.Conversation
import com.bitlove.fetlife.datasource.resource.ConversationListResource

class FetLifeDataSouce {

    fun loadConversations(forceLoad: Boolean, page: Int, limit: Int) : LiveData<List<Conversation>> {
        return ConversationListResource(forceLoad, page, limit).load()
    }

}