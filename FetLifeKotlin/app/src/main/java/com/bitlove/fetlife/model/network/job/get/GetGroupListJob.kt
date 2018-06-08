package com.bitlove.fetlife.model.network.job.get

import android.util.Log
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.content.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.content.GroupEntity
import com.bitlove.fetlife.model.dataobject.entity.content.RelationEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.db.dao.GroupDao
import com.bitlove.fetlife.model.db.dao.MemberDao
import retrofit2.Call

class GetGroupListJob(val limit: Int, val page: Int?, val memberId: String?, userId: String?) : GetListResourceJob<RelationEntity>(PRIORITY_GET_RESOURCE_FRONT,false, userId, TAG_GET_GROUPS, TAG_GET_RESOURCE) {

    companion object {
        const val TAG_GET_GROUPS = "TAG_GET_GROUPS"
    }

    override fun saveToDb(contentDb: FetLifeContentDatabase, resourceArray: Array<RelationEntity>) {
        val memberDao = contentDb.memberDao()
        val relationDao = contentDb.relationDao()
        val groupDao = contentDb.groupDao()

        var startServerOrder = (page!!-1)*limit

        var serverOrder = startServerOrder
        for (groupRelation in resourceArray) {
            saveGroupMember(groupRelation,memberDao)
            saveGroup(groupRelation,groupDao)

            groupRelation.relations = RelationEntity.Relation.GROUP_MEMBER.toString()

            val memberEntity = groupRelation.memberRef!!.asEntity()
            val groupEntity = groupRelation.groupRef
            val found = relationDao.getEntity(groupRelation!!.dbId)
            if (found != null) {
                val foundServerOrder = found.serverOrder
                if (foundServerOrder < serverOrder) {
                    serverOrder = foundServerOrder
                } else {
                    for (i in serverOrder until foundServerOrder) {
                        relationDao.deleteGroupRelationWithServerOrder(memberEntity.dbId,i)
                    }
                    relationDao.shiftGroupRelationServerOrder(memberEntity.dbId,foundServerOrder,serverOrder-foundServerOrder)
                }
            } else {
                relationDao.shiftGroupRelationServerOrder(memberEntity.dbId,serverOrder,1)
            }

            groupRelation.serverOrder = serverOrder++
            try {
                Log.e("JJJ","saving relation " + groupRelation.dbId)
                val member = memberDao.getEntity(groupRelation.memberId)
                Log.e("JJJ","is member exist " + (member != null).toString())
                val group = groupDao.getEntity(groupRelation.groupId!!)
                Log.e("JJJ","is group exist " + (group != null).toString())
                relationDao.insertOrUpdate(groupRelation)
                Log.e("JJJ","saving succeed")
                val relation = relationDao.getEntity(groupRelation.dbId!!)
                Log.e("JJJ","is relation exist " + (relation != null).toString())
                Log.e("JJJ","saving succeed: " + relationDao.getGroupRelationEntities().size)
            } catch (throwable : Throwable) {
                Log.e("JJJ","saving failed", throwable)
            }
        }
    }

    private fun saveGroupMember(groupRelation: RelationEntity, memberDao: MemberDao) {
        Log.e("JJJ","saving member " + groupRelation.memberRef?.nickname)
        val memberRef = groupRelation.memberRef
        if (memberRef != null) {
            val memberId = memberDao.update(memberRef)
            groupRelation.memberId = memberId
        }
        Log.e("JJJ","with id " + groupRelation.memberId)
        var member = memberDao.getEntity(groupRelation.memberId)
        Log.e("JJJ","is member exist " + (member != null).toString())
        member = memberDao.getEntity(groupRelation.memberId)
        Log.e("JJJ","is member exist " + (member != null).toString())
    }

    private fun saveGroup(groupRelation: RelationEntity, groupDao: GroupDao) {
        Log.e("JJJ","saving group " + groupRelation.groupRef?.name)
        val groupRef = groupRelation.groupRef
        if (groupRef != null) {
            groupDao.insertOrUpdate(groupRef)
            groupRelation.groupId = groupRef.dbId
            Log.e("JJJ","with id " + groupRef.dbId)
        }
        var group = groupDao.getEntity(groupRelation.groupId!!)
        Log.e("JJJ","is group exist " + (group != null).toString())
        group = groupDao.getEntity(groupRelation.groupId!!)
        Log.e("JJJ","is group exist " + (group != null).toString())
    }

    override fun getCall(): Call<Array<RelationEntity>> {
        return FetLifeApplication.instance.fetlifeService.fetLifeApi.getGroups(FetLifeApplication.instance.fetlifeService.authHeader!!,memberId,limit,page)
    }
}