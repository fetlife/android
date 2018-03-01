package com.bitlove.fetlife.datasource.network.job

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.datasource.dataobject.Conversation
import com.bitlove.fetlife.datasource.db.dao.BaseDao
import retrofit2.Call

class ConversationListJob() : ListResourceJob<Conversation>(BaseJob.PRIORITY_UI_FRONT,false, TAG_CONVERSATIONS, BaseJob.TAG_UI) {

    companion object {
        val TAG_CONVERSATIONS = "TAG_CONVERSATIONS"
    }

    override fun getDao(): BaseDao<Conversation> {
        return FetLifeApplication.instance.fetlifeDatabase.conversationDao()
    }

    override fun getCall(): Call<Array<Conversation>> {
        return FetLifeApplication.instance.fetlifeService.fetLifApi.getConversations("fsdfsf",null,null,null)
    }
}