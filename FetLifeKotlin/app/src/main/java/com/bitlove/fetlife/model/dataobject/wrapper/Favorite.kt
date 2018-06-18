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

class Favorite : CardViewDataHolder(), SyncObject<FavoriteEntity>, Favoritable {

    @Embedded
    lateinit var embeddedEntity: FavoriteEntity

    @Relation(parentColumn = "contentId", entityColumn = "dbId", entity = ContentEntity::class)
    var contents: List<Content>? = null

    @Relation(parentColumn = "memberId", entityColumn = "dbId", entity = MemberEntity::class)
    var members: List<Member>? = null

    override fun getAvatar(): AvatarViewDataHolder? {
        return getChild()?.getAvatar()
    }

    override fun getAvatarTitle(): String? {
        return getChild()?.getAvatarTitle()
    }

    override fun getType(): String? {
        return getChild()?.getType() ?: null
    }

    override fun getLocalId(): String? {
        return embeddedEntity.dbId
    }

    override fun getRemoteId(): String? {
        return getChild()?.getRemoteId()
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
        return getChild()?.getMediaUrl() ?: null
    }

    override fun getChild() : CardViewDataHolder? {
        if (contents?.isEmpty() == false) {
            return  contents!!.first()
        }
        if (members?.isEmpty() == false) {
            return  members!!.first()
        }
        return null
    }

    override fun getEntity(): FavoriteEntity {
        return embeddedEntity
    }

    override fun getDao(contentDb: FetLifeContentDatabase): BaseDao<FavoriteEntity> {
        return contentDb.favoriteDao()
    }

}