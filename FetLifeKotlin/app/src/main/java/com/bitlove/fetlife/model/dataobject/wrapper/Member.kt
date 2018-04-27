package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.content.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.content.RelationEntity
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.logic.dataholder.AvatarViewDataHolder
import com.bitlove.fetlife.model.db.FetLifeContentDatabase

class Member() : AvatarViewDataHolder(), SyncObject<MemberEntity> {

    @Ignore var subLine : String? = null
    @Ignore var subLineExtra : String? = null

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

    override fun getType(): String? {
        return Member::class.simpleName
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

    override fun getEntity(): MemberEntity {
        return memberEntity
    }

    override fun getDao(contentDb: FetLifeContentDatabase): BaseDao<MemberEntity> {
        return contentDb.memberDao()
    }

    override fun getAvatarSubline(): String? {
        return subLine
    }

    override fun getAvatarSublineExtra(): String? {
        return subLineExtra
    }
}