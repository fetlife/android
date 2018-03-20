package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content

@Dao
interface ContentDao : BaseDao<ContentEntity> {

    @Query("SELECT * FROM contents WHERE dbId = :dbId")
    fun getContent(dbId: String): LiveData<Content>

    @Query("SELECT * FROM contents WHERE type = 'CONVERSATION' ORDER BY updatedAt DESC")
    fun getConversations(): LiveData<List<Content>>

    @Query("SELECT * FROM contents WHERE memberId = :memberId")
    fun getMemberContent(memberId: String): LiveData<List<Content>>

    @Query("DELETE FROM contents")
    fun deleteAll()

}