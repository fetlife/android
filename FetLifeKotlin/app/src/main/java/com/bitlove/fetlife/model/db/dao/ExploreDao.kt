package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.bitlove.fetlife.model.dataobject.temp.ExploreStory

@Dao
interface ExploreDao : BaseDao<ExploreStory> {

    @Query("SELECT * FROM ExploreStory")
    fun getAll(): LiveData<List<ExploreStory>>

    @Query("DELETE FROM Conversation")
    fun deleteAll()
}