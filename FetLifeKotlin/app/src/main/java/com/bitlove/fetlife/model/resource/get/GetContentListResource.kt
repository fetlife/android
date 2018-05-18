package com.bitlove.fetlife.model.resource.get

import android.arch.paging.DataSource
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUserId
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.get.GetConversationListJob
import com.bitlove.fetlife.model.network.job.get.GetListResourceJob
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class GetContentListResource(val type: Content.TYPE, val forceLoad: Boolean, val limit: Int, userId : String? = getLoggedInUserId()) : GetListResource<Content>(userId, limit) {

    override fun loadListFromDb(contentDb: FetLifeContentDatabase): DataSource.Factory<Int, Content> {
        return when (type) {
            Content.TYPE.CONVERSATION -> contentDb.contentDao().getConversations()
            else -> {throw NotImplementedError()}
        }
    }

    override fun shouldSyncWithNetwork(item: Content?, frontItem: Boolean): Boolean {
        return forceLoad || item == null
    }

    override fun syncWithNetwork(item: Content?, frontItem: Boolean) {
        bg {
            var serverOrder = 0L
            if (item != null) {
                getContentDatabaseWrapper().safeRun(userId,{contentDb->
                    serverOrder = contentDb.contentDao().getContentServerOrder(item!!.getLocalId()!!)
                })
            }
            val job = when (type) {
                Content.TYPE.CONVERSATION -> {
                    GetConversationListJob(limit,(((serverOrder+1)/pageSize)+1).toInt(),item?.getCreatedAt(),userId)
                }
                else -> {throw NotImplementedError()}
            }
            addJob(job,false)
        }
    }

}