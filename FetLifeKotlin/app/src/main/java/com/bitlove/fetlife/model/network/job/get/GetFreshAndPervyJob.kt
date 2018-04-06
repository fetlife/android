package com.bitlove.fetlife.model.network.job.get

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.ExploreStoryEntity
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import retrofit2.Call

class GetFreshAndPervyJob : GetExploreListJob() {

    override fun getCall(): Call<Array<ExploreStoryEntity>> {
        return FetLifeApplication.instance.fetlifeService.fetLifeApi.getFreshAndPervy(FetLifeApplication.instance.fetlifeService.accessToken!!,null,25,1)
    }

    override fun getType(): ExploreStory.TYPE {
        return ExploreStory.TYPE.FRESH_AND_PERVY
    }
}