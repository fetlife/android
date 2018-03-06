package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.ConversationWithMessages

@Dao
interface ConversationWithMessagesDao {

    @Query("SELECT * FROM Conversation ORDER BY updatedAt DESC")
    fun getAll(): LiveData<List<ConversationWithMessages>>

}