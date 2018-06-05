package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreStoryEntity
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory

@Dao
abstract class ExploreStoryDao : BaseDao<ExploreStoryEntity> {

    @Query("SELECT * FROM explore_stories WHERE type=:type AND supported=1 ORDER BY serverOrder")
    abstract fun getStories(type: String): DataSource.Factory<Int,ExploreStory>

    @Query("SELECT * FROM explore_stories WHERE dbId = :storyId")
    abstract fun getStory(storyId: String): LiveData<ExploreStory>

    @Query("DELETE FROM explore_stories")
    abstract fun deleteAll()

    @Query("DELETE FROM explore_stories WHERE serverOrder = :serverOrder")
    abstract fun deleteWithServerOrder(serverOrder: Int)

    @Query("SELECT * FROM explore_stories WHERE type=:type ORDER BY serverOrder")
    abstract fun getEntities(type: String): List<ExploreStoryEntity>

    @Query("SELECT * FROM explore_stories WHERE dbId=:storyId")
    abstract fun getEntity(storyId: String): ExploreStoryEntity?

    @Query("UPDATE explore_stories SET serverOrder = serverOrder + :shiftWith WHERE type=:type AND serverOrder >= :shiftFrom")
    abstract fun shiftServerOrder(type: String, shiftFrom: Int, shiftWith: Int)


//    @Query("SELECT serverOrder FROM explore_stories WHERE dbId = :dbId")
//    abstract fun getStoryServerOrder(dbId: String): Long
//
//    @Query("SELECT * FROM explore_stories WHERE type=:type ORDER BY serverOrder DESC LIMIT 1")
//    abstract fun getLastStory(type: String): List<ExploreStoryEntity>
//
//    @Query("SELECT * FROM explore_stories WHERE type=:type AND serverOrder IN (:serverOrders) AND dbId NOT IN (:dbIds)")
//    abstract fun getConflictedEntities(type: String, serverOrders: List<Int>, dbIds: List<String>): List<ExploreStoryEntity>


}
