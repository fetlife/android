package com.bitlove.fetlife.model

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.network.job.syncresource.AddCommentJob
import com.bitlove.fetlife.model.resource.ConversationListResource

class FetLifeDataSource {

    fun loadConversations(forceLoad: Boolean, page: Int, limit: Int) : LiveData<List<Content>> {
        return ConversationListResource(forceLoad, page, limit).load()
    }

//    fun loadStuffYouLove(forceLoad: Boolean, page: Int, limit: Int): LiveData<List<ExploreStory>> {
//        return StuffYouLoveResource(forceLoad, page, limit).load()
//    }

    fun sendComment(comment: Reaction) {
        FetLifeApplication.instance.jobManager.addJobInBackground(AddCommentJob(comment))
    }

}