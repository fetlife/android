package com.bitlove.fetlife.model.network.job.getresource

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import retrofit2.Call

class GetMessageListJob(parent: SyncObject<ContentEntity>) : GetCommentListJob(parent) {

    override fun getCall(): Call<Array<ReactionEntity>> {
        return FetLifeApplication.instance.fetlifeService.fetLifApi.getMessages("fsdfsf",null,null,null, null)
    }
}