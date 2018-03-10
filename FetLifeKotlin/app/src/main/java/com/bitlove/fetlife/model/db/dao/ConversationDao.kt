package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.bitlove.fetlife.model.dataobject.base.Comment
import com.bitlove.fetlife.model.dataobject.base.Conversation

@Dao
interface ConversationDao : BaseDao<Conversation> {

    @Query("SELECT * FROM Conversation ORDER BY updatedAt DESC")
    fun getAll(): LiveData<List<Conversation>>

    @Query("DELETE FROM Conversation")
    fun deleteAll()
}