package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.LocalObject
import com.bitlove.fetlife.model.dataobject.entity.*
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.viewmodel.generic.AvatarViewDataHolder
import com.bitlove.fetlife.viewmodel.generic.CardViewDataHolder
import com.bitlove.fetlife.viewmodel.generic.ReactionViewDataHolder

class ExploreStory: CardViewDataHolder(), LocalObject<ExploreStoryEntity> {

    @Embedded
    lateinit var exploreStoryEntity: ExploreStoryEntity

    @Relation(parentColumn = "dbId", entityColumn = "storyId", entity = ExploreEventEntity::class)
    var exploreEvents: List<ExploreEvent>? = null

    override fun getTitle(): String? {
        return exploreStoryEntity?.type
    }

    override fun getMediaUrl(): String? {
        if (exploreEvents == null || exploreEvents!!.size != 1) return null
        return exploreEvents!!.first()?.getMediaUrl()
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

    override fun getDao(): BaseDao<ExploreStoryEntity> {
        return FetLifeApplication.instance.fetLifeDatabase.exploreStoryDao()
    }

}