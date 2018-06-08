package com.bitlove.fetlife.model.resource.get

import android.arch.paging.DataSource
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Group
import com.bitlove.fetlife.model.dataobject.wrapper.Relation
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.get.GetConversationListJob
import com.bitlove.fetlife.model.network.job.get.GetGroupListJob
import com.bitlove.fetlife.model.network.job.get.GetListResourceJob
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class GetGroupListResource(val forceLoad: Boolean, val limit: Int, val memberId: String? = getLoggedInUser()?.getNetworkId(), userId : String? = getLoggedInUser()?.getLocalId()) : GetListResource<Relation>(userId, limit) {

    override fun loadListFromDb(contentDb: FetLifeContentDatabase): DataSource.Factory<Int, Relation> {
        return contentDb.relationDao().getGroupRelations()
    }

    override fun syncWithNetwork(page: Int?, item: Relation?) {
        if (forceLoad) {
            addJob(GetGroupListJob(limit,page,memberId,userId),false)
        }
    }

}