package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction

@Dao
interface ReactionDao : BaseDao<ReactionEntity> {

    @Query("SELECT * FROM reactions WHERE dbId = :dbId")
    fun getReaction(dbId: String): LiveData<Reaction>

    @Query("DELETE FROM reactions")
    fun deleteAll()

}