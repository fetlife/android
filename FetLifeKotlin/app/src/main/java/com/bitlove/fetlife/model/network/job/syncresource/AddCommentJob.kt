package com.bitlove.fetlife.model.network.job.syncresource

import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import retrofit2.Call

class AddCommentJob(open val comment: SyncObject<ReactionEntity>) : SyncResourceJob<ReactionEntity>(comment, PRIORITY_MODIFY_RESOURCE,true, TAG_ADD_COMMENT, TAG_SYNC_RESOURCE) {

    companion object {
        const val TAG_ADD_COMMENT = "TAG_ADD_COMMENT"
    }

    override fun saveToDb(body: ReactionEntity?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCall(): Call<ReactionEntity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}