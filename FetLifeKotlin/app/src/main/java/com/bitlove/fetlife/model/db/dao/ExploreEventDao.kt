package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreEventEntity
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreEvent

@Dao
abstract class ExploreEventDao : BaseDao<ExploreEventEntity> {

    @Query("SELECT * FROM explore_events")
    abstract fun getEvents(): LiveData<List<ExploreEvent>>

    @Query("DELETE FROM explore_events")
    abstract fun deleteAll()

}