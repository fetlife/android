package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.bitlove.fetlife.model.dataobject.entity.content.GroupEntity
import com.bitlove.fetlife.model.dataobject.entity.content.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Group
import com.bitlove.fetlife.model.dataobject.wrapper.Member

@Dao
abstract class GroupDao : BaseDao<GroupEntity> {

    @Query("SELECT * FROM groups WHERE dbId = :dbId")
    abstract fun getEntity(dbId: String): GroupEntity?

    @Query("DELETE FROM groups")
    abstract fun deleteAll()

//    @Query("SELECT * FROM groups ORDER BY serverOrder")
//    abstract fun getGroups(): DataSource.Factory<Int, Group>
//
//    @Query("SELECT * FROM groups WHERE dbId = :dbId")
//    abstract fun getGroup(dbId: String): LiveData<Group>
//
//    @Query("SELECT * FROM groups WHERE dbId = :dbId")
//    abstract fun getEntity(dbId: String): GroupEntity?

}