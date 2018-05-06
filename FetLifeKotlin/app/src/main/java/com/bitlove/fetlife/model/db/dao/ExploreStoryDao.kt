package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreStoryEntity
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory

@Dao
abstract class ExploreStoryDao : BaseDao<ExploreStoryEntity> {

    @Query("SELECT * FROM explore_stories WHERE type=:type ORDER BY createdAt DESC")
    abstract fun getStories(type: String): DataSource.Factory<Int,ExploreStory>

    @Query("DELETE FROM explore_stories")
    abstract fun deleteAll()

    @Query("SELECT * FROM explore_stories WHERE dbId = :cardId")
    abstract fun getStory(cardId: String): LiveData<ExploreStory>
}