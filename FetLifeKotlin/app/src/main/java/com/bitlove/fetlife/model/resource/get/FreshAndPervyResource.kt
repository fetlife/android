package com.bitlove.fetlife.model.resource.get

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.network.job.get.GetExploreListJob
import com.bitlove.fetlife.model.network.job.get.GetFreshAndPervyJob
import com.bitlove.fetlife.model.network.job.get.GetStuffYouLoveJob

/**
 * Created by Titan on 28/03/2018.
 */
class FreshAndPervyResource(forceLoad: Boolean, page: Int, limit: Int) : ExploreResource(forceLoad, page, limit) {

    override fun syncWithNetwork(data: List<ExploreStory>?) {
        FetLifeApplication.instance.jobManager.addJobInBackground(GetFreshAndPervyJob())
    }

    override fun getType(): ExploreStory.TYPE {
        return ExploreStory.TYPE.FRESH_AND_PERVY
    }

}