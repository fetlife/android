package com.bitlove.fetlife.model.network.job.get

import com.bitlove.fetlife.model.dataobject.entity.content.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreEventEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreStoryEntity
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.model.dataobject.entity.reference.TargetRef
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.db.dao.ContentDao
import com.bitlove.fetlife.model.db.dao.MemberDao
import com.bitlove.fetlife.model.db.dao.ReactionDao
import com.bitlove.fetlife.model.db.dao.RelationDao
import com.bitlove.fetlife.model.network.networkobject.Feed
import retrofit2.Call
import retrofit2.Response

class GetExploreListJob(val type: ExploreStory.TYPE, val limit: Int, val page: Int, val marker : String? = null) : GetListResourceJob<ExploreStoryEntity>(PRIORITY_GET_RESOURCE_FRONT,false, TAG_EXPLORE, TAG_GET_RESOURCE) {

    companion object {
        //TODO separate tags
        const val TAG_EXPLORE = "TAG_EXPLORE"
    }

    //Workaround * for Feed vs Story Array
    override fun getCall(): Call<*> {
        return when(type) {
            ExploreStory.TYPE.FRESH_AND_PERVY -> getApi().getFreshAndPervy(getAuthHeader(),marker,limit,page)
            ExploreStory.TYPE.STUFF_YOU_LOVE -> getApi().getStuffYouLove(getAuthHeader(),marker,limit,page)
            ExploreStory.TYPE.KINKY_AND_POPULAR -> getApi().getKinkyAndPopular(getAuthHeader(),marker,limit,page)
            ExploreStory.TYPE.EXPLORE_FRIENDS -> getApi().getFriendsFeed(getAuthHeader(),marker,limit,page)
        }
    }

    //Workaround for Feed vs Story Array
    override fun getResultBody(result: Response<*>): Array<ExploreStoryEntity> {
        return when(type) {
            ExploreStory.TYPE.EXPLORE_FRIENDS -> {
                return (result as Response<Feed>).body()?.stories ?: arrayOf()
            }
            else -> super.getResultBody(result)
        }
    }

    override fun saveToDb(resourceArray: Array<ExploreStoryEntity>) {
        val exploreStoryDao = getDatabase().exploreStoryDao()
        val exploreEventDao = getDatabase().exploreEventDao()
        val memberDao = getDatabase().memberDao()
        val reactionDao = getDatabase().reactionDao()
        val relationDao = getDatabase().relationDao()
        val contentDao = getDatabase().contentDao()
        for ((i,story) in resourceArray.withIndex()) {
            if (!isSupported(story)) {
                continue
            }
            story.type = type.toString()
            //TODO: take care of page based ids in case of markers
            story.serverOrder = i + limit*(page-1)
            exploreStoryDao.insert(story)
            for (event in story.events!!) {
                event.storyId = story.dbId
                event.ownerId = saveMemberRef(event.memberRef, memberDao)
                saveEventTargets(event,memberDao,contentDao,reactionDao,relationDao)
                exploreEventDao.insert(event)
            }
        }
    }

    private fun isSupported(story: ExploreStoryEntity): Boolean {
        return if (story?.events?.isEmpty() != false) {
            false
        } else {
            return when (story.action) {
                "picture_created",
                "like_created" -> true
                else -> false
            }
        }
    }

    private fun saveEventTargets(event: ExploreEventEntity, memberDao: MemberDao, contentDao: ContentDao, reactionDao: ReactionDao, relationDao: RelationDao) {
        val target = event.target
        if (target != null) {
            saveEventTarget(event, target,memberDao,contentDao,reactionDao,relationDao)
        }
        val secondaryTarget = event.secondaryTarget
        if (secondaryTarget != null) {
            saveEventTarget(event, secondaryTarget,memberDao,contentDao,reactionDao,relationDao)
        }
    }

    private fun saveEventTarget(event: ExploreEventEntity, target: TargetRef, memberDao: MemberDao, contentDao: ContentDao, reactionDao: ReactionDao, relationDao: RelationDao) {
        if (target.picture != null) {
            val contentEntity : ContentEntity = target.picture.asEntity()
            val memberRef = target.picture.memberRef
            contentEntity.memberId = saveMemberRef(memberRef, memberDao)
            contentEntity.memberRemoteId = memberRef.id
            contentDao.insert(contentEntity)
            event.contentId = contentEntity.dbId
        }
    }

    private fun saveMemberRef(memberRef: MemberRef?, memberDao: MemberDao) : String {
        return if (memberRef != null) {
            memberDao.update(memberRef)
        } else ""
    }
}