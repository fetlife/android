package com.bitlove.fetlife.model

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.base.Comment
import com.bitlove.fetlife.model.dataobject.joined.ConversationWithMessages
import com.bitlove.fetlife.model.dataobject.temp.ExploreStory
import com.bitlove.fetlife.model.network.job.getresource.GetStuffYouLoveJob
import com.bitlove.fetlife.model.network.job.syncresource.AddCommentJob
import com.bitlove.fetlife.model.resource.ConversationListResource
import com.bitlove.fetlife.model.resource.StuffYouLoveResource
import org.jetbrains.anko.support.v4._ViewPager

class FetLifeDataSource {

    fun loadConversations(forceLoad: Boolean, page: Int, limit: Int) : LiveData<List<ConversationWithMessages>> {
        return ConversationListResource(forceLoad, page, limit).load()
    }

    fun loadStuffYouLove(forceLoad: Boolean, page: Int, limit: Int): LiveData<List<ExploreStory>> {
        return StuffYouLoveResource(forceLoad, page, limit).load()
    }

    fun sendComment(comment: Comment) {
        FetLifeApplication.instance.jobManager.addJobInBackground(AddCommentJob(comment))
    }

}