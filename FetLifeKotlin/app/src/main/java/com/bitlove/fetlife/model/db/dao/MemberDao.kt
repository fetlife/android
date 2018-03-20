package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.bitlove.fetlife.model.dataobject.entity.MemberEntity
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
        val currentMember = getMemberEntity(memberReference.id)
        return if (currentMember != null) {
            currentMember.nickname = memberReference.nickname
            currentMember.avatar = memberReference.avatar
            update(currentMember)
            currentMember.dbId
        } else {
            val member = MemberEntity()
            member.networkId = memberReference.id
            member.nickname = memberReference.nickname
            member.avatar = memberReference.avatar
            insert(member)
            member.dbId
        }
    }

    @Query("SELECT * FROM members WHERE dbId = :dbId")
    abstract fun getMemberEntity(dbId: String): MemberEntity?

}