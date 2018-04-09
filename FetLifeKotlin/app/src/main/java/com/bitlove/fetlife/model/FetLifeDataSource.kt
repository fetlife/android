package com.bitlove.fetlife.model

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.model.dataobject.entity.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.dataobject.wrapper.User
import com.bitlove.fetlife.model.resource.get.*
import com.bitlove.fetlife.model.resource.login.LoginResource
import com.bitlove.fetlife.model.resource.put.PutCommentResource
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import java.util.*

class FetLifeDataSource {

    fun loadConversations(forceLoad: Boolean, page: Int, limit: Int) : LiveData<List<Content>> {
        return ConversationListResource(forceLoad, page, limit).load()
    }

    fun loadStuffYouLove(forceLoad: Boolean, page: Int, limit: Int): LiveData<List<ExploreStory>> {
        return StuffYouLoveResource(forceLoad, page, limit).load()
    }

    fun loadFreshAndPervy(forceLoad: Boolean, page: Int, limit: Int): LiveData<List<ExploreStory>> {
        return FreshAndPervyResource(forceLoad, page, limit).load()
    }

    fun loadKinkyAndPopular(forceLoad: Boolean, page: Int, limit: Int): LiveData<List<ExploreStory>> {
        return KinkyAndPopularResource(forceLoad, page, limit).load()
    }

    fun sendComment(comment: String, content: CardViewDataHolder) {

        var commentReaction = ReactionEntity()
        commentReaction.type = Reaction.TYPE.COMMENT.toString()
        commentReaction.contentId = content.getLocalId()
        //TODO resolve memberId, network Id, and createdAt
        val memberEntity = MemberEntity()
        memberEntity.networkId = "106d3ab4"
        commentReaction.memberId = memberEntity.dbId
        commentReaction.networkId = UUID.randomUUID().toString()
        commentReaction.body = comment

        PutCommentResource().put(Reaction(commentReaction))
    }

    fun login(userName: String, password: String, rememberUser: Boolean): LiveData<List<User>> {
        return LoginResource().login(userName,password,rememberUser)
    }

}