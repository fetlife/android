package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.Comment
import com.bitlove.fetlife.model.dataobject.Conversation

@Dao
interface CommentDao : BaseDao<Conversation> {

    @Query("SELECT * FROM Comment WHERE commentForType = :commentForType AND commentForId = :commentForId ORDER BY createdAt DESC")
    fun getAll(commentForType: String, commentForId: String): LiveData<List<Comment>>

    @Query("DELETE FROM Comment")
    fun deleteAll()

}