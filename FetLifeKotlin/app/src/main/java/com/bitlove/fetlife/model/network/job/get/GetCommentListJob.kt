package com.bitlove.fetlife.model.network.job.get

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.db.dao.MemberDao
import retrofit2.Call

open class GetCommentListJob(open val parent: SyncObject<ContentEntity>) : GetListResourceJob<ReactionEntity>(PRIORITY_GET_RESOURCE_FRONT,false, TAG_GET_COMMENTS, TAG_GET_RESOURCE) {

    companion object {
        const val TAG_GET_COMMENTS = "TAG_GET_COMMENTS"
    }

    override fun saveToDb(resourceArray: Array<ReactionEntity>) {
        val memberDao = FetLifeApplication.instance.fetLifeContentDatabase!!.memberDao()
        for (reaction in resourceArray) {
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
        //TODO solve for other content types
        return FetLifeApplication.instance.fetlifeService.fetLifeApi.getComments(FetLifeApplication.instance.fetlifeService.accessToken!!, parent.getEntity()?.memberRemoteId, "pictures",parent.getRemoteId(),null,null, 5)
    }
}