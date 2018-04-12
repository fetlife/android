package com.bitlove.fetlife.model

import com.bitlove.fetlife.model.dataobject.entity.content.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ReactionEntity
import com.bitlove.fetlife.model.resource.get.*
import com.bitlove.fetlife.model.resource.login.LoginResource
import com.bitlove.fetlife.model.resource.put.PutReactionResource
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.interactionhandler.CardViewInteractionHandler
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.wrapper.*
import com.bitlove.fetlife.model.resource.ResourceResult
import org.apache.commons.lang3.Conversion
import java.util.*

class FetLifeDataSource {

    fun loadConversations(forceLoad: Boolean, page: Int, limit: Int) : ResourceResult<List<Content>> {
        return GetContentListResource(Content.TYPE.CONVERSATION, forceLoad, page, limit).load()
    }

    fun loadFriendsFeed(forceLoad: Boolean, page: Int, limit: Int): ResourceResult<List<ExploreStory>> {
        return GetExploreListResource(ExploreStory.TYPE.EXPLORE_FRIENDS,forceLoad, page, limit).load()
    }

    fun loadStuffYouLove(forceLoad: Boolean, page: Int, limit: Int): ResourceResult<List<ExploreStory>> {
        return GetExploreListResource(ExploreStory.TYPE.STUFF_YOU_LOVE,forceLoad, page, limit).load()
    }

    fun loadFreshAndPervy(forceLoad: Boolean, page: Int, limit: Int): ResourceResult<List<ExploreStory>> {
        return GetExploreListResource(ExploreStory.TYPE.FRESH_AND_PERVY,forceLoad, page, limit).load()
    }

    fun loadKinkyAndPopular(forceLoad: Boolean, page: Int, limit: Int): ResourceResult<List<ExploreStory>> {
        return GetExploreListResource(ExploreStory.TYPE.KINKY_AND_POPULAR,forceLoad, page, limit).load()
    }

    fun loadContentDetail(contentId: String): ResourceResult<Content> {
        return GetContentResource(contentId,true).load()
    }

    fun loadExploreStoryDetail(storyId: String): ResourceResult<ExploreStory> {
        return GetExploreResource(storyId, true).load()
    }

    fun loadComments(cardData: SyncObject<*>, page: Int, limit: Int): ResourceResult<List<Reaction>> {
        return GetReactionListResource(Reaction.TYPE.COMMENT,cardData,true, page, limit).load()
    }

    fun sendComment(comment: String, content: CardViewDataHolder) {
        var commentReaction = ReactionEntity()
        commentReaction.type = Reaction.TYPE.COMMENT.toString()
        commentReaction.contentId = content.getLocalId()

        //TODO(send_comment) resolve memberId, network Id, and createdAt
        val memberEntity = MemberEntity()
        memberEntity.networkId = "106d3ab4"
        commentReaction.memberId = memberEntity.dbId
        commentReaction.networkId = UUID.randomUUID().toString()
        commentReaction.body = comment

        PutReactionResource().put(Reaction(commentReaction))
    }

    fun login(userName: String, password: String, rememberUser: Boolean): ResourceResult<List<User>> {
        return LoginResource().login(userName,password,rememberUser)
    }

}