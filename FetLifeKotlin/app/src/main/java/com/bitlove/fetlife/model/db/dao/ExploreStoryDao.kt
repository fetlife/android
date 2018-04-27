package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreStoryEntity
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory

@Dao
abstract class ExploreStoryDao : BaseDao<ExploreStoryEntity> {

    @Query("SELECT * FROM explore_stories WHERE type=:type ORDER BY createdAt DESC limit 50")
    abstract fun getStories(type: String): LiveData<List<ExploreStory>>

    @Query("DELETE FROM explore_stories")
    abstract fun deleteAll()

    @Query("SELECT * FROM explore_stories WHERE dbId = :cardId")
    abstract fun getStory(cardId: String): LiveData<ExploreStory>
}