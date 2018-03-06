package com.bitlove.fetlife.model

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.Comment
import com.bitlove.fetlife.model.dataobject.ConversationWithMessages
import com.bitlove.fetlife.model.network.job.syncresource.AddCommentJob
import com.bitlove.fetlife.model.resource.ConversationListResource

class FetLifeDataSource {

    fun loadConversations(forceLoad: Boolean, page: Int, limit: Int) : LiveData<List<ConversationWithMessages>> {
        return ConversationListResource(forceLoad, page, limit).load()
    }

    fun sendComment(comment: Comment) {
        FetLifeApplication.instance.jobManager.addJobInBackground(AddCommentJob(comment))
    }

}