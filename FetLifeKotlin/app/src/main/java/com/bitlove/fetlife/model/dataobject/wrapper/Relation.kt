package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.logic.dataholder.AvatarViewDataHolder
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.dataholder.ReactionViewDataHolder
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.*
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.db.dao.BaseDao

class Relation : CardViewDataHolder(), SyncObject<RelationEntity>, Favoritable {

    @Embedded
    lateinit var relationEntity: RelationEntity

    @Relation(parentColumn = "groupId", entityColumn = "dbId", entity = GroupEntity::class)
    var singleGroupList: List<Group>? = null

    @Relation(parentColumn = "memberId", entityColumn = "dbId", entity = MemberEntity::class)
    var singleMemberList: List<Member>? = null

//    @Relation(parentColumn = "relatedMemberId", entityColumn = "dbId", entity = MemberEntity::class)
//    var singleTargetMemberList: List<Member>? = null

    override fun getAvatar(): AvatarViewDataHolder? {
        return null
    }

    override fun getType(): String? {
        return getChild()?.getType() ?: null
    }

    override fun getLocalId(): String? {
        return relationEntity.dbId
    }

    override fun getRemoteId(): String? {
        return relationEntity.dbId
    }

    override fun isLoved(): Boolean? {
        return getChild()?.isLoved() ?: null
    }

    override fun isFavorite(): Boolean? {
        return getChild()?.isFavorite() ?: null
    }

    override fun getFavoriteEntity(): FavoriteEntity? {
        return (getChild() as? Favoritable)?.getFavoriteEntity()
    }

    override fun getTitle(): String? {
        return getChild()?.getTitle()
    }

    override fun getSupportingText(): String? {
        return getChild()?.getSupportingText() ?: null
    }

    override fun getCreatedAt(): String? {
        return getChild()?.getCreatedAt() ?: null
    }

    override fun hasNewComment(): Boolean? {
        return getChild()?.hasNewComment() ?: null
    }

    override fun getLoveCount(): String? {
        return getChild()?.getLoveCount() ?: null
    }

    override fun getCommentCountText(): String? {
        return getChild()?.getCommentCountText() ?: null
    }

    override fun getComments(): List<ReactionViewDataHolder>? {
        return getChild()?.getComments() ?: null
    }

    override fun getThumbUrl(): String? {
        return getChild()?.getThumbUrl() ?: null
    }

    override fun getMediaUrl(): String? {
        return getChild()?.getMediaUrl() ?: null
    }

    override fun getMediaAspectRatio(): Float? {
        return getChild()?.getMediaAspectRatio() ?: null
    }

    override fun getUrl(): String? {
        return getChild()?.getUrl()
    }

    override fun getDao(contentDb: FetLifeContentDatabase): BaseDao<RelationEntity> {
        return contentDb.relationDao()
    }

    override fun getChild() : CardViewDataHolder? {
        if (singleGroupList?.size == 1) {
            return  singleGroupList!!.first()
//        } else if (singleTargetMemberList?.size == 1) {
//            return  singleTargetMemberList!!.first()
        }
        return null
    }

    override fun getEntity(): RelationEntity {
        return relationEntity
    }



}