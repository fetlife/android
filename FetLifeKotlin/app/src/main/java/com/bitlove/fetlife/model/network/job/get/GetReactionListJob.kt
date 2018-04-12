package com.bitlove.fetlife.model.network.job.get

import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ReactionEntity
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.db.dao.MemberDao
import org.apache.commons.lang3.Conversion
import retrofit2.Call

open class GetReactionListJob(val type: Reaction.TYPE, val parent: SyncObject<ContentEntity>, val limit: Int, val page: Int, val sinceMarker: String? = null, val unilMarker: String? = null) : GetListResourceJob<ReactionEntity>(PRIORITY_GET_RESOURCE_FRONT,false, TAG_GET_COMMENTS, TAG_GET_RESOURCE) {

    companion object {
        const val TAG_GET_COMMENTS = "TAG_GET_COMMENTS"
    }

    override fun saveToDb(resourceArray: Array<ReactionEntity>) {
        val memberDao = getDatabase().memberDao()
        for ((i,reaction) in resourceArray.withIndex()) {
            //TODO(paging) : improve server oder
            reaction.contentId = parent.getLocalId()
            reaction.memberId = saveMember(reaction.memberRef, memberDao)
            reaction.type = Reaction.TYPE.COMMENT.toString()
        }
        getDatabase().reactionDao().insert(*resourceArray)
        parent.save()
    }

    private fun saveMember(memberRef: MemberRef?, memberDao: MemberDao) : String? {
        return if (memberRef != null) {
            memberDao.update(memberRef)
        } else
            null
    }


    override fun getCall(): Call<Array<ReactionEntity>> {
        //TODO(cleanup) solve for other content types
        return when (type) {
            Reaction.TYPE.COMMENT -> {
                if (parent is Content && parent.getType() == Content.TYPE.CONVERSATION.toString()) {
                    getApi().getMessages(getAuthHeader(),parent.getRemoteId(),sinceMarker,unilMarker,limit,page)
                } else {
                    getApi().getComments(getAuthHeader(),parent.getEntity().memberRemoteId,"pictures",parent.getRemoteId(),sinceMarker,unilMarker,limit,page)
                }
            }
            else -> {throw IllegalArgumentException()}
        }
    }
}