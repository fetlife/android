package com.bitlove.fetlife.model.network.job.get

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.db.dao.MemberDao
import com.bitlove.fetlife.model.db.dao.ReactionDao
import retrofit2.Call

class GetConversationListJob : GetListResourceJob<ContentEntity>(PRIORITY_GET_RESOURCE_FRONT,false, TAG_GET_CONVERSATIONS, TAG_GET_RESOURCE) {

    companion object {
        const val TAG_GET_CONVERSATIONS = "TAG_GET_CONVERSATIONS"
    }

    override fun saveToDb(resourceArray: Array<ContentEntity>) {
        val memberDao = getDatabase().memberDao()
        val reactionDao = getDatabase().reactionDao()
        val contentDao = getDatabase().contentDao()
        for (content in resourceArray) {
            saveContentMember(content,memberDao)
            content.type = Content.TYPE.CONVERSATION.toString()
            contentDao.insert(content)
            saveLastMessage(content,reactionDao,memberDao)
        }
//        getDatabase().contentDao().insert(*resourceArray)
    }

    private fun saveLastMessage(content: ContentEntity, reactionDao: ReactionDao, memberDao: MemberDao) {
        val lastMessage = content.lastMessage
        if (lastMessage != null) {
            val memberRef = lastMessage.memberRef
            val memberId = if (memberRef != null) {
                memberDao.update(memberRef)
            } else {
                null
            }
            reactionDao.update(lastMessage, Reaction.TYPE.COMMENT, content.dbId, memberId)
        }
    }

    private fun saveContentMember(content: ContentEntity, memberDao: MemberDao) {
        val memberRef = content.memberRef
        if (memberRef != null) {
            val memberId = memberDao.update(memberRef)
            content.memberId = memberId
        }
    }

    override fun getCall(): Call<Array<ContentEntity>> {
        return FetLifeApplication.instance.fetlifeService.fetLifeApi.getConversations(FetLifeApplication.instance.fetlifeService.authToken!!,null,25,1)
    }
}