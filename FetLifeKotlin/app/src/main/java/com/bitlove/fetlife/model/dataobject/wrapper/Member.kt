package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.RelationEntity
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.viewmodel.generic.AvatarViewDataHolder

class Member() : AvatarViewDataHolder(), SyncObject<MemberEntity> {

    constructor(memberEntity: MemberEntity) : this() {
        this.memberEntity = memberEntity
    }

    @Embedded lateinit var memberEntity: MemberEntity

    @Relation(parentColumn = "dbId", entityColumn = "memberId", entity = ContentEntity::class)
    var memberContent: List<ContentEntity>? = null

    @Relation(parentColumn = "dbId", entityColumn = "memberId", entity = RelationEntity::class)
    var relations: List<RelationEntity>? = null

    override fun getLocalId(): String? {
        return memberEntity?.dbId
    }

    override fun getRemoteId(): String? {
        return memberEntity?.networkId
    }

    override fun getAvatarUrl(): String? {
        return memberEntity?.avatar?.variants?.medium
    }

    override fun getAvatarName(): String? {
        return memberEntity?.nickname
    }

    override fun getAvatarMeta(): String? {
        return memberEntity?.metaInfo
    }

    override fun getEntity(): MemberEntity? {
        return memberEntity
    }

    override fun getDao(): BaseDao<MemberEntity> {
        return getDataBase().memberDao()
    }
}