package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import android.provider.SyncStateContract.Helpers.insert
import android.util.Log
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.ExploreEventEntity
import com.bitlove.fetlife.model.dataobject.entity.ExploreStoryEntity
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import com.bitlove.fetlife.model.dataobject.entity.reference.ReactionRef
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction

@Dao
abstract class ExploreEventDao : BaseDao<ExploreEventEntity> {

    @Query("SELECT * FROM explore_events ORDER BY createdAt DESC")
    abstract fun getEvents(): LiveData<List<ExploreStory>>

    @Query("DELETE FROM explore_events")
    abstract fun deleteAll()

}