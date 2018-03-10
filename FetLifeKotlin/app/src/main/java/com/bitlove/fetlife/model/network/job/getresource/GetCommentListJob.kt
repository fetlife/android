package com.bitlove.fetlife.model.network.job.getresource

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.DataObject
import com.bitlove.fetlife.model.dataobject.base.Comment
import com.bitlove.fetlife.model.dataobject.base.Conversation
import com.bitlove.fetlife.model.db.dao.BaseDao
import retrofit2.Call

class GetCommentListJob<out T : DataObject>(private val parent: T) : GetListResourceJob<Comment>(PRIORITY_GET_RESOURCE_FRONT,false, TAG_GET_COMMENTS, TAG_GET_RESOURCE) {

    companion object {
        const val TAG_GET_COMMENTS = "TAG_GET_COMMENTS"
    }

    override fun saveToDb(resourceArray: Array<Comment>) {
        for (comment in resourceArray) {
            comment.parentId = parent.getAppId()
        }
        super.saveToDb(resourceArray)
        FetLifeApplication.instance.fetlifeDatabase.conversationDao().update(parent as Conversation)
    }

    override fun getDao(): BaseDao<Comment> {
        return FetLifeApplication.instance.fetlifeDatabase.commentDao()
    }

    override fun getCall(): Call<Array<Comment>> {
        return FetLifeApplication.instance.fetlifeService.fetLifApi.getMessages("das",parent.getServerId(),"","",0)
    }
}