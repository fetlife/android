package com.bitlove.fetlife.model.network.job.get

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import retrofit2.Call

open class GetCommentListJob(private val parent: SyncObject<ContentEntity>) : GetListResourceJob<ReactionEntity>(PRIORITY_GET_RESOURCE_FRONT,false, TAG_GET_COMMENTS, TAG_GET_RESOURCE) {

    companion object {
        const val TAG_GET_COMMENTS = "TAG_GET_COMMENTS"
    }

    override fun saveToDb(resourceArray: Array<ReactionEntity>) {
        for (reaction in resourceArray) {
            reaction.contentId = parent.getLocalId()
            reaction.type = Reaction.TYPE.COMMENT.toString()
        }
        getDatabase().reactionDao().insert(*resourceArray)
        parent.save()
    }

    override fun getCall(): Call<Array<ReactionEntity>> {
        //TODO return comment call
        return FetLifeApplication.instance.fetlifeService.fetLifeApi.getMessages("das",parent.getRemoteId(),"","",0)
    }
}