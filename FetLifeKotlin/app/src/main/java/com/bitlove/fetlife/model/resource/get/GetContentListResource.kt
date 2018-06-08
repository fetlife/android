package com.bitlove.fetlife.model.resource.get

import android.arch.paging.DataSource
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.get.GetConversationListJob
import com.bitlove.fetlife.model.network.job.get.GetListResourceJob
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class GetContentListResource(val type: Content.TYPE, val forceLoad: Boolean, val limit: Int, userId : String? = getLoggedInUser()?.getLocalId()) : GetListResource<Content>(userId, limit) {

    override fun loadListFromDb(contentDb: FetLifeContentDatabase): DataSource.Factory<Int, Content> {
        return when (type) {
            Content.TYPE.CONVERSATION -> contentDb.contentDao().getConversations()
            else -> {throw NotImplementedError()}
        }
    }

    override fun syncWithNetwork(page: Int?, item: Content?) {
        if (forceLoad) {
            val job = when (type) {
                Content.TYPE.CONVERSATION -> {
                    GetConversationListJob(limit,page,userId)
                }
                else -> {throw NotImplementedError()}
            }
            addJob(job,false)
        }
    }

}