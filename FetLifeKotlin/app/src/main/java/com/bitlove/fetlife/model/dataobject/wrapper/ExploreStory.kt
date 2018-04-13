package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.logic.dataholder.AvatarViewDataHolder
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.dataholder.ReactionViewDataHolder
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreEventEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreStoryEntity

class ExploreStory: CardViewDataHolder(), SyncObject<ExploreStoryEntity> {

    enum class TYPE {FRESH_AND_PERVY, KINKY_AND_POPULAR, STUFF_YOU_LOVE, EXPLORE_FRIENDS}

    @Embedded
    lateinit var exploreStoryEntity: ExploreStoryEntity

    @Relation(parentColumn = "dbId", entityColumn = "storyId", entity = ExploreEventEntity::class)
    var exploreEvents: List<ExploreEvent>? = null

    override fun getTitle(): String? {
        return exploreStoryEntity?.action
    }

    override fun getChildren(): List<CardViewDataHolder>? {
        return if (exploreEvents?.size == 1) {
            null
        } else {
            exploreEvents
        }
    }

    override fun getCommentCountText(): String? {
        return if (exploreEvents?.size == 1) {
            exploreEvents!!.first().getCommentCountText()
        } else {
            null
        }
    }

    override fun hasNewComment(): Boolean? {
        return if (exploreEvents?.size == 1) {
            exploreEvents!!.first().hasNewComment()
        } else {
            null
        }
    }

    override fun getLoveCount(): String? {
        return if (exploreEvents?.size == 1) {
            exploreEvents!!.first().getLoveCount()
        } else {
            null
        }
    }

    override fun isLoved(): Boolean? {
        return if (exploreEvents?.size == 1) {
            exploreEvents!!.first().isLoved()
        } else {
            null
        }
    }

    override fun isDeletable(): Boolean? {
        return false
    }

    override fun getMediaUrl(): String? {
        return if (exploreEvents?.size == 1) {
            exploreEvents!!.first().getMediaUrl()
        } else {
            null
        }
    }

    override fun getMediaAspectRatio(): Float? {
        return if (exploreEvents?.size == 1) {
            exploreEvents!!.first().getMediaAspectRatio()
        } else {
            null
        }
    }

    override fun getSupportingText(): String? {
        return if (exploreEvents?.size == 1) {
            exploreEvents!!.first().getSupportingText()
        } else {
            null
        }
    }

    override fun getComments(): List<ReactionViewDataHolder>? {
        return if (exploreEvents?.size == 1) {
            exploreEvents!!.first().getComments()
        } else {
            null
        }
    }

    override fun getAvatar(): AvatarViewDataHolder? {
        return if (exploreEvents?.isEmpty() == false) {
            exploreEvents!!.first().getAvatar()
        } else {
            null
        }
    }

    override fun getLocalId(): String? {
        return exploreStoryEntity?.dbId
    }

    override fun getRemoteId(): String? {
        return exploreStoryEntity?.dbId
    }

    override fun getType(): String? {
        return exploreStoryEntity.type
    }

    override fun getChild(): CardViewDataHolder? {
        return if (exploreEvents?.isEmpty() == false) {
            exploreEvents!!.first().getChild()
        } else {
            null
        }
    }

    override fun getEntity(): ExploreStoryEntity {
        return exploreStoryEntity
    }

    override fun getDao(): BaseDao<ExploreStoryEntity> {
        return FetLifeApplication.instance.fetLifeContentDatabase.exploreStoryDao()
    }

}