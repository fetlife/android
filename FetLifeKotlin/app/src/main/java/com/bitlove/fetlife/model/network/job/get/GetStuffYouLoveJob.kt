package com.bitlove.fetlife.model.network.job.get

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.ExploreEventEntity
import com.bitlove.fetlife.model.dataobject.entity.ExploreStoryEntity
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.model.dataobject.entity.reference.TargetRef
import com.bitlove.fetlife.model.db.dao.ContentDao
import com.bitlove.fetlife.model.db.dao.MemberDao
import com.bitlove.fetlife.model.db.dao.ReactionDao
import com.bitlove.fetlife.model.db.dao.RelationDao
import retrofit2.Call

class GetStuffYouLoveJob : GetListResourceJob<ExploreStoryEntity>(PRIORITY_GET_RESOURCE_FRONT,false, TAG_GET_STUFF_YOU_LOVE, TAG_GET_RESOURCE) {

    companion object {
        const val TAG_GET_STUFF_YOU_LOVE = "TAG_GET_STUFF_YOU_LOVE"
    }

    override fun saveToDb(resourceArray: Array<ExploreStoryEntity>) {
        val exploreStoryDao = getDatabase().exploreStoryDao()
        val exploreEventDao = getDatabase().exploreEventDao()
        val memberDao = getDatabase().memberDao()
        val reactionDao = getDatabase().reactionDao()
        val relationDao = getDatabase().relationDao()
        val contentDao = getDatabase().contentDao()
        for (story in resourceArray) {
            if (story?.events == null) {
                continue
            }
            exploreStoryDao.insert(story)
            for (event in story.events!!) {
                event.storyId = story.dbId
                event.memberId = saveMemberRef(event.memberRef, memberDao)
                saveEventTargets(event,memberDao,contentDao,reactionDao,relationDao)
                exploreEventDao.insert(event)
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
            contentEntity.memberId = saveMemberRef(target.picture.memberRef, memberDao)
            contentDao.insert(contentEntity)
            event.contentId = contentEntity.dbId
        }
    }

    private fun saveMemberRef(memberRef: MemberRef?, memberDao: MemberDao) : String {
        return if (memberRef != null) {
            memberDao.update(memberRef)
        } else ""
    }

    override fun getCall(): Call<Array<ExploreStoryEntity>> {
        return FetLifeApplication.instance.fetlifeService.fetLifeApi.getStuffYouLove(FetLifeApplication.instance.fetlifeService.authToken!!,null,25,1)
    }
}