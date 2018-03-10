package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.base.Comment
import com.bitlove.fetlife.model.dataobject.base.Conversation

@Dao
interface CommentDao : BaseDao<Comment> {

    @Query("SELECT * FROM Comment WHERE parentId = :parentId ORDER BY createdAt DESC")
    fun getAll(parentId: String): LiveData<List<Comment>>

    @Query("DELETE FROM Comment")
    fun deleteAll()

}