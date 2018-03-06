package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.Conversation

@Dao
interface ConversationDao : BaseDao<Conversation> {

    @Query("SELECT * FROM Conversation ORDER BY updatedAt DESC")
    fun getAll(): LiveData<List<Conversation>>

    @Query("DELETE FROM Conversation")
    fun deleteAll()
}