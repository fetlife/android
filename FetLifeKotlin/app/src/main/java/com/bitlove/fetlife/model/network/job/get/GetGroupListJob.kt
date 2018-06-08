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
                relationDao.insertOrUpdate(groupRelation)

            } catch (throwable : Throwable) {

            }
        }
    }

    private fun saveGroupMember(groupRelation: RelationEntity, memberDao: MemberDao) {
        val memberRef = groupRelation.memberRef
        if (memberRef != null) {
            val memberId = memberDao.update(memberRef)
            groupRelation.memberId = memberId
        }
    }

    private fun saveGroup(groupRelation: RelationEntity, groupDao: GroupDao) {
        Log.e("JJJ","saving group " + groupRelation.groupRef?.name)
        val groupRef = groupRelation.groupRef
        if (groupRef != null) {
            groupDao.insertOrUpdate(groupRef)
            groupRelation.groupId = groupRef.dbId
            Log.e("JJJ","with id " + groupRef.dbId)
        }
    }

    override fun getCall(): Call<Array<RelationEntity>> {
        return FetLifeApplication.instance.fetlifeService.fetLifeApi.getGroups(FetLifeApplication.instance.fetlifeService.authHeader!!,memberId,limit,page)
    }
}