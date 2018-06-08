package com.bitlove.fetlife.model.network.job.get

import android.util.Log
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.content.ContentEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.db.dao.MemberDao
import com.bitlove.fetlife.model.db.dao.ReactionDao
import org.apache.commons.lang3.Conversion
import retrofit2.Call

class GetConversationListJob(val limit: Int, val page: Int?, userId: String?) : GetListResourceJob<ContentEntity>(PRIORITY_GET_RESOURCE_FRONT,false, userId, TAG_GET_CONVERSATIONS, TAG_GET_RESOURCE) {

    companion object {
        const val TAG_GET_CONVERSATIONS = "TAG_GET_CONVERSATIONS"
    }

    override fun saveToDb(contentDb: FetLifeContentDatabase, resourceArray: Array<ContentEntity>) {
        val memberDao = contentDb.memberDao()
        val reactionDao = contentDb.reactionDao()
        val contentDao = contentDb.contentDao()

        var startServerOrder = (page!!-1)*limit

        var serverOrder = startServerOrder
        for (conversation in resourceArray) {

            conversation.type = Content.TYPE.CONVERSATION.toString()

            val found = contentDao.getEntity(conversation.dbId)
            if (found != null) {
                val foundServerOrder = found.serverOrder
                if (foundServerOrder < serverOrder) {
                    serverOrder = foundServerOrder
                } else {
                    for (i in serverOrder until foundServerOrder) {
                        contentDao.deleteWithServerOrder(Content.TYPE.CONVERSATION.toString(),i)
                    }
                    contentDao.shiftServerOrder(Content.TYPE.CONVERSATION.toString(),foundServerOrder,serverOrder-foundServerOrder)
                }
            } else {
                contentDao.shiftServerOrder(Content.TYPE.CONVERSATION.toString(),serverOrder,1)
            }

            conversation.serverOrder = serverOrder++
            saveContentMember(conversation,memberDao)
            contentDao.insertOrUpdate(conversation)
            saveLastMessage(conversation,reactionDao,memberDao)
        }
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
            content.remoteMemberId = memberRef.id
        }
    }

    override fun getCall(): Call<Array<ContentEntity>> {
//        Log.e("XXX",page.toString())
        return FetLifeApplication.instance.fetlifeService.fetLifeApi.getConversations(FetLifeApplication.instance.fetlifeService.authHeader!!,null,limit,page)
    }
}