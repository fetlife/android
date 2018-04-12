package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getBaseUrl
import com.bitlove.fetlife.hash
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

    @Ignore
    var remoteHash: String? = null

    override fun getTitle(): String? {
        return exploreStoryEntity?.action
    }

    override fun getCommentCountText(): String? {
        if (exploreEvents == null || exploreEvents!!.size != 1) return null
        return exploreEvents!!.first()?.getCommentCountText()
    }

    override fun hasNewComment(): Boolean? {
        return false
    }

    override fun getLoveCount(): String? {
        if (exploreEvents == null || exploreEvents!!.size != 1) return null
        return exploreEvents!!.first()?.getLoveCount()
    }

    override fun isLoved(): Boolean? {
        if (exploreEvents == null || exploreEvents!!.size != 1) return null
        return exploreEvents!!.first()?.isLoved()
    }

    override fun isDeletable(): Boolean? {
        return false
    }

    override fun getMediaUrl(): String? {
        if (exploreEvents == null || exploreEvents!!.size != 1) return null
        return exploreEvents!!.first()?.getMediaUrl()
    }

    override fun getMediaAspectRatio(): Float? {
        if (exploreEvents == null || exploreEvents!!.size != 1) return null
        return exploreEvents!!.first()?.getMediaAspectRatio()
    }

    override fun getSupportingText(): String? {
        if (exploreEvents == null || exploreEvents!!.size != 1) return null
        return exploreEvents!!.first()?.getSupportingText()
    }

    override fun getComments(): List<ReactionViewDataHolder>? {
        return if (exploreEvents != null && exploreEvents!!.size == 1) exploreEvents!!.first().getComments() else null
    }

    override fun getAvatar(): AvatarViewDataHolder? {
        return exploreEvents?.firstOrNull()?.getMember()
    }

    override fun getLocalId(): String? {
        return exploreStoryEntity?.dbId
    }

    override fun getRemoteId(): String? {
        if (remoteHash == null) {
            remoteHash = (getAvatar()?.getAvatarName() + getTitle() + getSupportingText() + getMediaUrl()?.getBaseUrl() + getEntity()?.type + getEntity().createdAt).hash()
        }
        return remoteHash!!
    }

    override fun getType(): String? {
        return exploreEvents?.firstOrNull()?.getType()
    }

    override fun getEntity(): ExploreStoryEntity {
        return exploreStoryEntity
    }


    override fun getDao(): BaseDao<ExploreStoryEntity> {
        return FetLifeApplication.instance.fetLifeContentDatabase.exploreStoryDao()
    }

    fun getContent(): Content? {
        return exploreEvents?.firstOrNull()?.getContent()
    }

}