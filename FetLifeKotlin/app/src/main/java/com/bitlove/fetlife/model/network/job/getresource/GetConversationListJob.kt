package com.bitlove.fetlife.model.network.job.getresource

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import retrofit2.Call

class GetConversationListJob : GetListResourceJob<ContentEntity>(PRIORITY_GET_RESOURCE_FRONT,false, TAG_GET_CONVERSATIONS, TAG_GET_RESOURCE) {

    companion object {
        val TAG_GET_CONVERSATIONS = "TAG_GET_CONVERSATIONS"
    }

    override fun saveToDb(resourceArray: Array<ContentEntity>) {
        val memberDao = getDatabase().memberDao()
        for (content in resourceArray) {
            val memberRef = content.memberRef
            if (memberRef != null) {
                val memberId = memberDao.update(memberRef)
                content.memberId = memberId
            }
            content.type = Content.TYPE.CONVERSATION.toString()
        }
        getDatabase().contentDao().insert(*resourceArray)
    }

    override fun getCall(): Call<Array<ContentEntity>> {
        return FetLifeApplication.instance.fetlifeService.fetLifApi.getConversations("fsdfsf",null,null,null)
    }
}