package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.logic.dataholder.AvatarViewDataHolder
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.dataholder.ReactionViewDataHolder
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.*
import com.bitlove.fetlife.model.db.FetLifeContentDatabase

class ExploreEvent : CardViewDataHolder(), SyncObject<ExploreEventEntity>, Favoritable {
    @Embedded
    lateinit var exploreEventEntity: ExploreEventEntity

    @Relation(parentColumn = "ownerId", entityColumn = "dbId", entity = MemberEntity::class)
    var ownerSingleItemList: List<Member>? = null

    @Relation(parentColumn = "contentId", entityColumn = "dbId", entity = ContentEntity::class)
    var contents: List<Content>? = null

    @Relation(parentColumn = "reactionId", entityColumn = "dbId", entity = ReactionEntity::class)
    var reactions: List<Reaction>? = null

    @Relation(parentColumn = "memberId", entityColumn = "dbId", entity = MemberEntity::class)
    var members: List<Member>? = null

    override fun getAvatar(): AvatarViewDataHolder? {
        return ownerSingleItemList?.firstOrNull()
    }

    override fun getType(): String? {
        return getChild()?.getType() ?: null
    }

    override fun getLocalId(): String? {
        return exploreEventEntity.dbId
    }

    override fun getRemoteId(): String? {
        return exploreEventEntity.dbId
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

    override fun getDao(contentDb: FetLifeContentDatabase): BaseDao<ExploreEventEntity> {
        return contentDb.exploreEventDao()
    }

    override fun getChild() : CardViewDataHolder? {
        if (contents?.size == 1) {
            return  contents!!.first()
        }
        if (reactions?.size == 1) {
            return  reactions!!.first()
        }
        if (members?.size == 1) {
            return  members!!.first()
        }
        return null
    }

    override fun getEntity(): ExploreEventEntity {
        return exploreEventEntity
    }


}