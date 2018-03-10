package com.bitlove.fetlife.model.network.job.getresource

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.base.Conversation
import com.bitlove.fetlife.model.dataobject.temp.ExploreStory
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.model.db.dao.ConversationWithMessagesDao
import retrofit2.Call

class GetStuffYouLoveJob : GetListResourceJob<ExploreStory>(PRIORITY_GET_RESOURCE_FRONT,false, TAG_GET_STUFF_YOU_LOVE, TAG_GET_RESOURCE) {

    companion object {
        val TAG_GET_STUFF_YOU_LOVE = "TAG_GET_STUFF_YOU_LOVE"
    }

    override fun getDao(): BaseDao<ExploreStory> {
        return FetLifeApplication.instance.fetlifeDatabase.exploreDao()
    }

    override fun getCall(): Call<Array<ExploreStory>> {
        return FetLifeApplication.instance.fetlifeService.fetLifApi.getStuffYouLove("fsdfsf","", null,null)
    }
}