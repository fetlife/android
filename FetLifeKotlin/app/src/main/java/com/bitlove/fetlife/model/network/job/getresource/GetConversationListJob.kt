package com.bitlove.fetlife.model.network.job.getresource

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.base.Conversation
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.model.db.dao.ConversationWithMessagesDao
import retrofit2.Call

class GetConversationListJob : GetListResourceJob<Conversation>(PRIORITY_GET_RESOURCE_FRONT,false, TAG_GET_CONVERSATIONS, TAG_GET_RESOURCE) {

    companion object {
        val TAG_GET_CONVERSATIONS = "TAG_GET_CONVERSATIONS"
    }

    override fun saveToDb(resourceArray: Array<Conversation>) {
        (getDao() as ConversationWithMessagesDao).insertWithLastMessage(*resourceArray)
    }

    override fun getDao(): BaseDao<Conversation> {
        return FetLifeApplication.instance.fetlifeDatabase.conversationWithMessagesDao()
    }

    override fun getCall(): Call<Array<Conversation>> {
        return FetLifeApplication.instance.fetlifeService.fetLifApi.getConversations("fsdfsf",null,null,null)
    }
}