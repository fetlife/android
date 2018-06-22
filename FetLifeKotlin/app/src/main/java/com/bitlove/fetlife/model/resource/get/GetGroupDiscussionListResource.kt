package com.bitlove.fetlife.model.resource.get

import android.arch.paging.DataSource
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.GroupEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Group
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.get.GetConversationListJob
import com.bitlove.fetlife.model.network.job.get.GetGroupDiscussionListJob
import com.bitlove.fetlife.model.network.job.get.GetListResourceJob
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class GetGroupDiscussionListResource(val forceLoad: Boolean, val limit: Int, val groupId: String, userId : String? = getLoggedInUser()?.getLocalId()) : GetListResource<Content>(userId, limit) {

    override fun loadListFromDb(contentDb: FetLifeContentDatabase): DataSource.Factory<Int, Content> {
        return contentDb.contentDao().getGroupDiscussions(groupId)
    }

    override fun syncWithNetwork(page: Int?, item: Content?) {
        if (forceLoad) {
            //TODO (GROUPS): remove temp solution
            FetLifeApplication.instance.fetLifeContentDatabaseWrapper.safeRun(getLoggedInUser()?.getLocalId(), {
                contentDb ->
                val remoteId = contentDb.groupDao().getEntity(groupId)?.networkId
                if (remoteId != null) addJob(GetGroupDiscussionListJob(remoteId,limit,page,userId),false)
            }, false)
        }
    }

}