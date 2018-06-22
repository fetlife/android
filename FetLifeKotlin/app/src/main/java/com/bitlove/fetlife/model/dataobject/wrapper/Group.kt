package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.logic.dataholder.AvatarViewDataHolder
import com.bitlove.fetlife.model.dataobject.entity.content.*
import com.bitlove.fetlife.model.db.FetLifeContentDatabase

class Group() : AvatarViewDataHolder(), SyncObject<GroupEntity>, Favoritable {

    constructor(groupEntity: GroupEntity) : this() {
        this.groupEntity = groupEntity
    }

    @Embedded lateinit var groupEntity: GroupEntity

    @Relation(parentColumn = "dbId", entityColumn = "groupId", entity = ContentEntity::class)
    var groupContent: List<ContentEntity>? = null

    @Relation(parentColumn = "dbId", entityColumn = "groupId", entity = RelationEntity::class)
    var relations: List<RelationEntity>? = null

    @Relation(parentColumn = "dbId", entityColumn = "groupId", entity = FavoriteEntity::class)
    var favoriteSingleItem: List<FavoriteEntity>? = null

    override fun getLocalId(): String? {
        return groupEntity?.dbId
    }

    override fun getRemoteId(): String? {
        return groupEntity?.networkId
    }

    override fun getTitle(): String? {
        return groupEntity?.name
    }

    override fun getSupportingText(): String? {
        return groupEntity?.description
    }

    override fun getMemberCount(): String? {
        return groupEntity?.memberCount.toString()
    }

    override fun isMember(): Boolean? {
        return groupEntity.memberOfGroup
    }

    override fun getType(): String? {
        return Group::class.simpleName
    }

    override fun getEntity(): GroupEntity {
        return groupEntity
    }

    override fun getDao(contentDb: FetLifeContentDatabase): BaseDao<GroupEntity> {
        return contentDb.groupDao()
    }

    override fun getUrl(): String? {
        return groupEntity?.url
    }

    override fun isFavorite(): Boolean? {
        return favoriteSingleItem?.firstOrNull() != null
    }

    override fun getFavoriteEntity(): FavoriteEntity? {
        return favoriteSingleItem?.firstOrNull()
    }

}