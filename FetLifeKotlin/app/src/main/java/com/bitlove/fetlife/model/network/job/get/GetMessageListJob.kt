package com.bitlove.fetlife.model.network.job.get

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import retrofit2.Call

class GetMessageListJob(parent: SyncObject<ContentEntity>) : GetCommentListJob(parent) {

    override fun getCall(): Call<Array<ReactionEntity>> {
        return FetLifeApplication.instance.fetlifeService.fetLifeApi.getMessages("fsdfsf",null,null,null, null)
    }
}