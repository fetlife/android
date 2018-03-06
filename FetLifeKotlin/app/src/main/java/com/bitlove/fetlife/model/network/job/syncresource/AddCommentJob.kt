package com.bitlove.fetlife.model.network.job.syncresource

import com.bitlove.fetlife.model.dataobject.Comment
import com.bitlove.fetlife.model.db.dao.BaseDao
import retrofit2.Call

class AddCommentJob(open val comment: Comment) : SyncResourceJob<Comment>(comment, PRIORITY_MODIFY_RESOURCE,true, TAG_ADD_COMMENT, TAG_SYNC_RESOURCE) {

    companion object {
        const val TAG_ADD_COMMENT = "TAG_ADD_COMMENT"
    }

    override fun getCall(): Call<Comment> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDao(): BaseDao<Comment> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}