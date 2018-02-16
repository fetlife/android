package com.bitlove.fetlife.datasource.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.datasource.dataobject.Conversation
import io.reactivex.Flowable

@Dao
interface ConversationDao : BaseDao<Conversation> {

//    @Query("SELECT * FROM Conversation ORDER BY updatedAt DESC")
//    fun getAllOrderedByLastUpdated(): Flowable<List<Conversation>>

    @Query("SELECT * FROM Conversation ORDER BY updatedAt DESC")
    fun getAllOrderedByLastUpdated(): LiveData<List<Conversation>>

    @Query("DELETE FROM Conversation")
    fun deleteAll()
}