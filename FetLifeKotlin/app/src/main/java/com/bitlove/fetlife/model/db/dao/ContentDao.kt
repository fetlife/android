package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import android.util.Log
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content

@Dao
abstract class ContentDao : BaseDao<ContentEntity> {

    @Query("SELECT * FROM contents WHERE dbId = :dbId")
    abstract fun getContent(dbId: String): LiveData<Content>

    @Query("SELECT * FROM contents WHERE type = 'CONVERSATION' ORDER BY updatedAt DESC")
    abstract fun getConversations(): LiveData<List<Content>>

    @Query("SELECT * FROM contents WHERE memberId = :memberId")
    abstract fun getMemberContent(memberId: String): LiveData<List<Content>>

    @Query("DELETE FROM contents")
    abstract fun deleteAll()

    @Query("SELECT * FROM contents WHERE dbId = :dbId")
    abstract fun getContentEntity(dbId: String): ContentEntity

}