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

class GetConversationListJob(val limit: Int, val page: Int?, val marker : Content? = null, userId: String?) : GetListResourceJob<ContentEntity>(PRIORITY_GET_RESOURCE_FRONT,false, userId, TAG_GET_CONVERSATIONS, TAG_GET_RESOURCE) {

    companion object {
        const val TAG_GET_CONVERSATIONS = "TAG_GET_CONVERSATIONS"
    }

    override fun saveToDb(contentDb: FetLifeContentDatabase, resourceArray: Array<ContentEntity>) {
        val memberDao = contentDb.memberDao()
        val reactionDao = contentDb.reactionDao()
        val contentDao = contentDb.contentDao()

        //merge
        //TODO: cleanup
        var serverOrders = ArrayList<Int>()
        var dbIds = ArrayList<String>()
        var startServerOrder = if (marker != null) marker!!.getEntity().serverOrder+1 else (page!!-1)*limit
        for (i in startServerOrder until startServerOrder + limit) {
            serverOrders.add(i)
        }
        for (content in resourceArray) {
            content.type = Content.TYPE.CONVERSATION.toString()
            dbIds.add(content.dbId)
        }
        var conflictedConversations = contentDao.getConflictedConversations(serverOrders,dbIds)
        var shiftWith = 0; var shiftFrom = startServerOrder + limit
        for (conflictedConversation in conflictedConversations.reversed()) {
            if (conflictedConversation.serverOrder == shiftFrom-1) {
                shiftFrom--;shiftWith++
            } else {
                contentDao.delete(conflictedConversation)
            }
        }
        if (shiftWith > 0) {
            contentDao.shiftServerOrder(shiftFrom,shiftWith)
        }
        //mergeEnd

        Log.e("ZZZ","page: " + page.toString())
        Log.e("ZZZ","marker: " + marker)

        var serverOrderCheck = contentDao.getConversationsServerOrder()
        var log = "count: " + serverOrderCheck.size + " //// "
        for (so in serverOrderCheck) {
            log += so.dbId + "-" + so.serverOrder + ", "
        }

        Log.e("ZZZ",log)

        var serverOrder = startServerOrder
        for ((i,content) in resourceArray.withIndex()) {
            content.serverOrder = serverOrder++
            saveContentMember(content,memberDao)
            content.type = Content.TYPE.CONVERSATION.toString()
            contentDao.insertOrUpdate(content)
            saveLastMessage(content,reactionDao,memberDao)
        }
//        contenDb.contentDao().insert(*resourceArray)

        serverOrderCheck = contentDao.getConversationsServerOrder()
        log = "count: " + serverOrderCheck.size + " //// "
        for (so in serverOrderCheck) {
            log += so.dbId + "-" + so.serverOrder + ", "
        }
        Log.e("ZZZ",log)

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
        Log.e("XXX",page.toString())
        return FetLifeApplication.instance.fetlifeService.fetLifeApi.getConversations(FetLifeApplication.instance.fetlifeService.authHeader!!,null,limit,page)
    }
}