package com.bitlove.fetlife.model.network.job.get

import android.util.Log
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.content.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.content.GroupEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.db.dao.MemberDao
import com.bitlove.fetlife.model.db.dao.ReactionDao
import org.apache.commons.lang3.Conversion
import retrofit2.Call

class GetGroupDiscussionListJob(val groupId: String, val limit: Int, val page: Int?, userId: String?) : GetListResourceJob<ContentEntity>(PRIORITY_GET_RESOURCE_FRONT,false, userId, TAG_GET_GROUP_DISCUSSIONS, TAG_GET_RESOURCE) {

    companion object {
        const val TAG_GET_GROUP_DISCUSSIONS = "TAG_GET_GROUP_DISCUSSIONS"
    }

    override fun saveToDb(contentDb: FetLifeContentDatabase, resourceArray: Array<ContentEntity>) {
        val memberDao = contentDb.memberDao()
        val reactionDao = contentDb.reactionDao()
        val contentDao = contentDb.contentDao()

        var startServerOrder = (page!!-1)*limit

        var serverOrder = startServerOrder
        for (discussion in resourceArray) {

            discussion.type = Content.TYPE.GROUP_DISCUSSION.toString()

            val found = contentDao.getEntity(discussion.dbId)
            if (found != null) {
                val foundServerOrder = found.serverOrder
                if (foundServerOrder < serverOrder) {
                    serverOrder = foundServerOrder
                } else {
                    for (i in serverOrder until foundServerOrder) {
                        contentDao.deleteWithServerOrder(Content.TYPE.GROUP_DISCUSSION.toString(),i)
                    }
                    contentDao.shiftServerOrder(Content.TYPE.GROUP_DISCUSSION.toString(),foundServerOrder,serverOrder-foundServerOrder)
                }
            } else {
                contentDao.shiftServerOrder(Content.TYPE.GROUP_DISCUSSION.toString(),serverOrder,1)
            }

            val groupEntity = GroupEntity()
            groupEntity.networkId = groupId

            discussion.groupId = groupEntity.dbId
            discussion.parentNetworkId = groupId
            discussion.serverOrder = serverOrder++
            saveContentMember(discussion,memberDao)
            contentDao.insertOrUpdate(discussion)
        }
    }

    private fun saveContentMember(content: ContentEntity, memberDao: MemberDao) {
        val memberRef = content.memberRef
        if (memberRef != null) {
            val memberId = memberDao.update(memberRef)
            content.memberId = memberId
            content.remoteMemberId = memberRef.id
        }
    }

    override fun getCall(): Call<Array<ContentEntity>> {
//        Log.e("XXX",page.toString())
        return FetLifeApplication.instance.fetlifeService.fetLifeApi.getGroupDiscussions(FetLifeApplication.instance.fetlifeService.authHeader!!, groupId,limit,page)
    }
}