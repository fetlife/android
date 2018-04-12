package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.network.job.get.GetConversationListJob
import com.bitlove.fetlife.model.network.job.get.GetListResourceJob

class GetContentListResource(val type: Content.TYPE, forceLoad: Boolean, val page: Int, val limit: Int) : GetResource<List<Content>>(forceLoad) {

    private val contentDao = FetLifeApplication.instance.fetLifeContentDatabase.contentDao()

    override fun loadFromDb(): LiveData<List<Content>> {
        return when (type) {
            Content.TYPE.CONVERSATION -> contentDao.getConversations()
            else -> {throw NotImplementedError()}
        }
    }

    override fun shouldSync(data: List<Content>?, forceSync: Boolean): Boolean {
        //TODO : Consider using expiration time
        return forceSync
    }

    override fun syncWithNetwork(data: List<Content>?) {
        val job : GetListResourceJob<*> = when (type) {
            Content.TYPE.CONVERSATION -> GetConversationListJob()
            else -> {throw NotImplementedError()}
        }
        setProgressTracker(job.progressTrackerLiveData)
        FetLifeApplication.instance.jobManager.addJobInBackground(job)
    }
}