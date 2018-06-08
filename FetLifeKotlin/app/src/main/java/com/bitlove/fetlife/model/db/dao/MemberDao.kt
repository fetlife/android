package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.bitlove.fetlife.model.dataobject.entity.content.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.model.dataobject.wrapper.Member

@Dao
abstract class MemberDao : BaseDao<MemberEntity> {

    @Query("SELECT * FROM members WHERE dbId = :dbId")
    abstract fun getMember(dbId: String): LiveData<Member>

    @Query("DELETE FROM members")
    abstract fun deleteAll()

    @Transaction
    open fun update(memberReference: MemberRef) : String {
        val referenceEntity = memberReference.asEntity()
        val currentMember = getEntity(referenceEntity.dbId)
        if (currentMember != null) {
            update(memberReference.asEntity(currentMember))
        } else {
            insert(referenceEntity)
        }
        return referenceEntity.dbId
    }

    @Query("SELECT * FROM members WHERE dbId = :dbId")
    abstract fun getEntity(dbId: String): MemberEntity?

}