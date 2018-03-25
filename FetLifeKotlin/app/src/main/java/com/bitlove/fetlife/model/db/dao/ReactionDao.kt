package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import android.util.Log
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import com.bitlove.fetlife.model.dataobject.entity.reference.ReactionRef
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction

@Dao
abstract class ReactionDao : BaseDao<ReactionEntity> {

    @Transaction
    open fun update(reactionReference: ReactionRef, reactionType: Reaction.TYPE, contentId: String, memberId: String?) {
        //TODO consider update in th future
        val reaction = reactionReference.asEntity()
        reaction.contentId = contentId
        reaction.memberId = memberId
        reaction.type = reactionType.toString()
        insert(reaction)
    }

    @Query("SELECT * FROM reactions WHERE dbId = :dbId")
    abstract fun getReaction(dbId: String): LiveData<Reaction>

    @Query("DELETE FROM reactions")
    abstract fun deleteAll()

}