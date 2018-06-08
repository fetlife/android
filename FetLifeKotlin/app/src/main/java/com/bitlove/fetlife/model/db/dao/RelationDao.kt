package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.entity.content.RelationEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Relation

@Dao
abstract class RelationDao : BaseDao<RelationEntity> {

    @Query("SELECT * FROM relations")
    abstract fun getGroupRelations(): DataSource.Factory<Int, Relation>

//    @Query("SELECT * FROM relations WHERE memberId=:memberId AND groupId IS NOT NULL ORDER BY serverOrder")
//    abstract fun getGroupRelations(memberId: String): DataSource.Factory<Int, Relation>

    @Query("SELECT * FROM relations WHERE dbId = :dbId")
    abstract fun getGroupRelation(dbId: String): LiveData<Relation>

    @Query("SELECT * FROM relations WHERE dbId = :dbId")
    abstract fun getEntity(dbId: String): RelationEntity

    @Query("UPDATE relations SET serverOrder = serverOrder + :shiftWith WHERE memberId=:memberId AND groupId IS NOT NULL AND serverOrder >= :shiftFrom")
    abstract fun shiftGroupRelationServerOrder(memberId: String, shiftFrom: Int, shiftWith: Int)

    @Query("DELETE FROM relations WHERE memberId=:memberId AND groupId IS NOT NULL AND serverOrder = :serverOrder")
    abstract fun deleteGroupRelationWithServerOrder(memberId: String, serverOrder: Int)


}