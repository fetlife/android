package com.bitlove.fetlife.model

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.resource.get.*
import com.bitlove.fetlife.model.resource.login.LoginResource
import com.bitlove.fetlife.model.resource.post.PostReactionResource
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.wrapper.*
import com.bitlove.fetlife.model.network.delete.DeleteReactionJob
import com.bitlove.fetlife.model.resource.ResourceResult
import com.bitlove.fetlife.model.resource.delete.DeleteReactionResource
import com.bitlove.fetlife.model.resource.post.SetFavoriteResource

class FetLifeDataSource {

    fun getConversationsLoader(forceLoad: Boolean, page: Int, limit: Int) : ResourceResult<List<Content>> {
        return GetContentListResource(Content.TYPE.CONVERSATION, forceLoad, page, limit).loadResult
    }

    fun getFriendsFeedLoader(forceLoad: Boolean, page: Int, limit: Int): ResourceResult<List<ExploreStory>> {
        return GetExploreListResource(ExploreStory.TYPE.EXPLORE_FRIENDS,forceLoad, page, limit).loadResult
    }

    fun getStuffYouLoveLoader(forceLoad: Boolean, page: Int, limit: Int): ResourceResult<List<ExploreStory>> {
        return GetExploreListResource(ExploreStory.TYPE.STUFF_YOU_LOVE,forceLoad, page, limit).loadResult
    }

    fun getFreshAndPervyLoader(forceLoad: Boolean, page: Int, limit: Int): ResourceResult<List<ExploreStory>> {
        return GetExploreListResource(ExploreStory.TYPE.FRESH_AND_PERVY,forceLoad, page, limit).loadResult
    }

    fun getKinkyAndPopularLoader(forceLoad: Boolean, page: Int, limit: Int): ResourceResult<List<ExploreStory>> {
        return GetExploreListResource(ExploreStory.TYPE.KINKY_AND_POPULAR,forceLoad, page, limit).loadResult
    }

    fun getFavoritesLoader(forceLoad: Boolean, page: Int, limit: Int): ResourceResult<List<Favorite>> {
        return GetFavoriteListResource(forceLoad,page,limit).loadResult
    }

    fun getContentDetailLoader(contentId: String): ResourceResult<Content> {
        return GetContentResource(contentId,true).loadResult
    }

    fun getExploreStoryDetailLoader(storyId: String): ResourceResult<ExploreStory> {
        return GetExploreStoryResource(storyId, true).loadResult
    }

    fun getExploreEventDetailLoader(eventId: String): ResourceResult<ExploreEvent> {
        return GetExploreEventResource(eventId, true).loadResult
    }

    fun getFavoriteDetailLoader(favId: String): ResourceResult<Favorite> {
        return GetFavoriteResource(favId, true).loadResult
    }

    fun getCommentsLoader(cardData: SyncObject<*>, page: Int, limit: Int): ResourceResult<List<Reaction>> {
        return GetReactionListResource(Reaction.TYPE.COMMENT,cardData,true, page, limit).loadResult
    }

    fun sendLove(content: Content) : ResourceResult<Reaction> {
        return PostReactionResource.newPostLoveResource(content).loadResult
    }

    fun removeLove(content: Content) : ResourceResult<Reaction> {
        return DeleteReactionResource.newDeleteLoveResource(content).loadResult
    }

    fun setFavorite(cardData: Favoritable) : ResourceResult<Favoritable> {
        return SetFavoriteResource(cardData).loadResult
    }

    fun sendComment(comment: String, content: Content) : ResourceResult<Reaction> {
        return PostReactionResource.newPostCommentResource(comment,content).loadResult
    }

    fun login(userName: String, password: String, rememberUser: Boolean): ResourceResult<List<User>> {
        return LoginResource(userName,password,rememberUser).loadResult
    }

}