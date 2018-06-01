package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.entity.content.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreStoryEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content

@Dao
abstract class ContentDao : BaseDao<ContentEntity> {

    @Query("SELECT * FROM contents WHERE dbId = :dbId")
    abstract fun getContent(dbId: String): LiveData<Content>

    @Query("SELECT * FROM contents WHERE type = 'CONVERSATION' ORDER BY serverOrder")
    abstract fun getConversations(): DataSource.Factory<Int,Content>

    @Query("SELECT * FROM contents WHERE type = 'CONVERSATION' ORDER BY serverOrder")
    abstract fun getConversationsServerOrder(): List<ContentEntity>

    @Query("SELECT * FROM contents WHERE memberId = :memberId")
    abstract fun getMemberContent(memberId: String):  DataSource.Factory<Int,Content>

    @Query("DELETE FROM contents")
    abstract fun deleteAll()

    @Query("SELECT * FROM contents WHERE dbId = :dbId")
    abstract fun getContentEntity(dbId: String): ContentEntity

    @Query("SELECT serverOrder FROM contents WHERE dbId = :dbId")
    abstract fun getContentServerOrder(dbId: String): Long

    @Query("SELECT * FROM contents WHERE serverOrder IN (:serverOrders) AND dbId NOT IN (:dbIds)")
    abstract fun getConflictedConversations(serverOrders: List<Int>, dbIds: List<String>): List<ContentEntity>

    @Query("UPDATE contents SET serverOrder = serverOrder + :shiftWith WHERE serverOrder >= :shiftFrom")
    abstract fun shiftServerOrder(shiftFrom: Int, shiftWith: Int)

}