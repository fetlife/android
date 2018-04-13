package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.logic.dataholder.AvatarViewDataHolder
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.dataholder.ReactionViewDataHolder
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.*

class ExploreEvent : CardViewDataHolder(), SyncObject<ExploreEventEntity> {
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

    override fun isLoved(): Boolean? {
        return getChild()?.isLoved() ?: null
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

    override fun getMediaUrl(): String? {
        return getChild()?.getMediaUrl() ?: null
    }

    override fun getMediaAspectRatio(): Float? {
        return getChild()?.getMediaAspectRatio() ?: null
    }

    override fun getDao(): BaseDao<ExploreEventEntity> {
        return FetLifeApplication.instance.fetLifeContentDatabase.exploreEventDao()
    }

    override fun getChild() : CardViewDataHolder? {
        if (contents?.isEmpty() == false) {
            return  contents!!.first()
        }
        if (reactions?.isEmpty() == false) {
            return  reactions!!.first()
        }
        if (members?.isEmpty() == false) {
            return  members!!.first()
        }
        return null
    }

    override fun getEntity(): ExploreEventEntity {
        return exploreEventEntity
    }


}