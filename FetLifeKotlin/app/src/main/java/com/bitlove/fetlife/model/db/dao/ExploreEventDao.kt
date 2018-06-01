package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreEventEntity
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreEvent

@Dao
abstract class ExploreEventDao : BaseDao<ExploreEventEntity> {

    @Query("SELECT * FROM explore_events")
    abstract fun getEvents(): DataSource.Factory<Int,ExploreEvent>

    @Query("DELETE FROM explore_events WHERE ownerId=:storyId AND dbId NOT IN (:eventIds)")
    abstract fun deleteObsoleteEvents(storyId: String, eventIds: List<String>)

    @Query("DELETE FROM explore_events")
    abstract fun deleteAll()

    @Query("SELECT * FROM explore_events WHERE dbId = :cardId")
    abstract fun getEvent(cardId: String): LiveData<ExploreEvent>

}