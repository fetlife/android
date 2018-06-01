package com.bitlove.fetlife.model.network.job.get

import com.bitlove.fetlife.getLoggedInUserId
import com.bitlove.fetlife.model.dataobject.entity.content.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreEventEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreStoryEntity
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.model.dataobject.entity.reference.TargetRef
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.db.dao.ContentDao
import com.bitlove.fetlife.model.db.dao.MemberDao
import com.bitlove.fetlife.model.db.dao.ReactionDao
import com.bitlove.fetlife.model.db.dao.RelationDao
import com.bitlove.fetlife.model.network.networkobject.Feed
import com.bitlove.fetlife.parseServerTime
import retrofit2.Call
import retrofit2.Response

class GetExploreListJob(val type: ExploreStory.TYPE, val limit: Int, val page: Int?, var marker : ExploreStory? = null, userId: String?) : GetListResourceJob<ExploreStoryEntity>(PRIORITY_GET_RESOURCE_FRONT,false, userId, TAG_EXPLORE, TAG_GET_RESOURCE) {

    private var markerTimeStamp : String? = null

    init {
        if (marker != null && page == 1) {
            marker = null
        } else if (marker != null) {
            markerTimeStamp = (marker!!.getCreatedAt()?.parseServerTime()?:0 * 1000L).toString()
        }
    }

    companion object {
        //TODO separate tags
        const val TAG_EXPLORE = "TAG_EXPLORE"
    }

    //Workaround * for Feed vs Story Array
    override fun getCall(): Call<*> {
        return when(type) {
            ExploreStory.TYPE.FRESH_AND_PERVY -> getApi().getFreshAndPervy(getAuthHeader(),markerTimeStamp,limit,page)
            ExploreStory.TYPE.STUFF_YOU_LOVE -> getApi().getStuffYouLove(getAuthHeader(),markerTimeStamp,limit,page)
            ExploreStory.TYPE.KINKY_AND_POPULAR -> getApi().getKinkyAndPopular(getAuthHeader(),markerTimeStamp,limit,page)
            //TODO: user marker
            ExploreStory.TYPE.EXPLORE_FRIENDS -> getApi().getFriendsFeed(getAuthHeader(),null,limit,page)
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

    override fun saveToDb(contenDb: FetLifeContentDatabase, resourceArray: Array<ExploreStoryEntity>) {
        val exploreStoryDao = contenDb.exploreStoryDao()
        val exploreEventDao = contenDb.exploreEventDao()
        val memberDao = contenDb.memberDao()
        val reactionDao = contenDb.reactionDao()
        val relationDao = contenDb.relationDao()
        val contentDao = contenDb.contentDao()

        //merge
        //TODO: cleanup
        var serverOrders = ArrayList<Int>()
        var dbIds = ArrayList<String>()

        var startServerOrder = if (marker != null) marker!!.getEntity().serverOrder+1 else (page!!-1)*limit
        for (i in startServerOrder until startServerOrder + limit) {
            serverOrders.add(i)
        }
        for ((i,story) in resourceArray.withIndex()) {
            story.type = type.toString()
            story.createdAt = story.events?.firstOrNull()?.target?.createdAt
            dbIds.add(story.dbId)
        }
        var conflictedStories = exploreStoryDao.getConflictedEntities(type.toString(),serverOrders,dbIds)
        var shiftWith = 0; var shiftFrom = startServerOrder + limit
        for (conflictedStory in conflictedStories.reversed()) {
            if (conflictedStory.serverOrder == shiftFrom-1) {
                shiftFrom--;shiftWith++
            } else {
                exploreStoryDao.delete(conflictedStory)
            }
        }
        if (shiftWith > 0) {
            exploreStoryDao.shiftServerOrder(type.toString(),shiftFrom,shiftWith)
        }
        //mergeEnd

        //workaround
//        var lastStory : ExploreStoryEntity? = null

        var serverOrder = startServerOrder
        for ((i,story) in resourceArray.withIndex()) {
//            lastStory = story
            story.type = type.toString()
            story.createdAt = story.events?.firstOrNull()?.target?.createdAt
            story.serverOrder = serverOrder++
            exploreStoryDao.insertOrUpdate(story)

            if (!isSupported(story)) {
                continue
            }

            val eventIds = ArrayList<String>()
            for (event in story.events!!) {
                event.type = type.toString()
                event.createdAt = event.target?.createdAt
                event.storyId = story.dbId
                event.ownerId = saveMemberRef(event.memberRef, memberDao)
                saveEventTargets(event,memberDao,contentDao,reactionDao,relationDao)
                eventIds.add(event.dbId)
                exploreEventDao.insertOrUpdate(event)
            }
            exploreEventDao.deleteObsoleteEvents(story.dbId,eventIds)
        }

//        if (lastStory == null) {
//            lastStory = exploreStoryDao.getLastStory(type.toString()).firstOrNull()
//        }
//
//        if (lastStory != null) {
//            lastStory?.serverOrder = serverOrder-1
//            exploreStoryDao.insertOrUpdate(lastStory)
//        }
    }

    private fun isSupported(story: ExploreStoryEntity): Boolean {
        return if (story?.events?.isEmpty() != false) {
            false
        } else {
            return when (story.action) {
                //TODO remove workaround from ExploreStory
                "post_created",
                "picture_created",
                "like_created" -> {
                    if (story.events!![0]?.target?.picture != null || story.events!![0]?.secondaryTarget?.picture != null) {
                        true
                    } else if (story.events!!.size > 1 ){
                        false
                    } else {
                        story.events!![0]?.target?.writing != null ||  story.events!![0]?.secondaryTarget?.writing != null
                    }
                }
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
        var memberRef : MemberRef? = null
        val contentEntity : ContentEntity? =
        when {
            target.picture != null -> {
                memberRef = target.picture!!.memberRef
                target.picture!!.asEntity()
            }
            target.writing != null -> {
                memberRef = target.writing!!.memberRef
                target.writing!!.asEntity()
            }
            else -> return
        }
        contentEntity!!.memberId = saveMemberRef(memberRef, memberDao)
        contentEntity!!.remoteMemberId = memberRef?.id
        contentDao.insertOrUpdate(contentEntity)
        event.contentId = contentEntity.dbId
    }

    private fun saveMemberRef(memberRef: MemberRef?, memberDao: MemberDao) : String {
        return if (memberRef != null) {
            memberDao.update(memberRef)
        } else ""
    }
}