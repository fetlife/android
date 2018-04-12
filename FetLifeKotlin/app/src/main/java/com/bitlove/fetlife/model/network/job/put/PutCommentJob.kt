package com.bitlove.fetlife.model.network.job.put

import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.ReactionEntity
import retrofit2.Call

//TODO: solve persistence
class PutCommentJob(comment: SyncObject<ReactionEntity>) : PutResourceJob<ReactionEntity>(comment, PRIORITY_MODIFY_RESOURCE,false, TAG_ADD_COMMENT, TAG_SYNC_RESOURCE) {

    companion object {
        const val TAG_ADD_COMMENT = "TAG_ADD_COMMENT"
    }

    override fun saveToDb(entity: ReactionEntity) {
        getDatabase().reactionDao().insert(entity)
    }

    override fun getCall(): Call<ReactionEntity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}