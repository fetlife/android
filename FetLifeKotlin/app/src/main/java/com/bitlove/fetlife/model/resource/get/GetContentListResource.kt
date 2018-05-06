package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLivePagesList
import com.bitlove.fetlife.getLoggedInUserId
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.get.GetConversationListJob
import com.bitlove.fetlife.model.network.job.get.GetListResourceJob

class GetContentListResource(val type: Content.TYPE, val forceLoad: Boolean, val limit: Int, userId : String? = getLoggedInUserId()) : GetListResource<Content>(userId, limit) {

    override fun loadListFromDb(contentDb: FetLifeContentDatabase): DataSource.Factory<Int, Content> {
        return when (type) {
            Content.TYPE.CONVERSATION -> contentDb.contentDao().getConversations()
            else -> {throw NotImplementedError()}
        }
    }

    override fun shouldSyncWithNetwork(itemAtEnd: Content?, itemAndReached: Int): Boolean {
        return forceLoad || itemAtEnd == null
    }

    override fun syncWithNetwork(itemAtEnd: Content?, itemAndReached: Int) {
        val job : GetListResourceJob<*> = when (type) {
            Content.TYPE.CONVERSATION -> GetConversationListJob(limit,itemAndReached+1,null,userId)
            else -> {throw NotImplementedError()}
        }
        setProgressTracker(job.progressTrackerLiveData)
        FetLifeApplication.instance.jobManager.addJobInBackground(job)
    }

}