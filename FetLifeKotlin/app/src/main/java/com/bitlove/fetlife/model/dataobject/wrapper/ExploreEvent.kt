package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.LocalObject
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.ExploreEventEntity
import com.bitlove.fetlife.model.dataobject.entity.ExploreStoryEntity
import com.bitlove.fetlife.model.dataobject.entity.MemberEntity
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.viewmodel.generic.CardViewDataHolder

class ExploreEvent : CardViewDataHolder(), LocalObject<ExploreEventEntity> {

    @Embedded
    lateinit var exploreEventEntity: ExploreEventEntity

    @Relation(parentColumn = "memberId", entityColumn = "dbId", entity = MemberEntity::class)
    var ownerSingleItemList: List<Member>? = null

    @Relation(parentColumn = "contentId", entityColumn = "dbId", entity = ContentEntity::class)
    var contents: List<Content>? = null

    fun getMember() : Member? {
        return ownerSingleItemList?.firstOrNull()
    }

    override fun getComments(): List<Reaction>? {
        return if (contents != null && contents!!.size == 1) contents!!.first()?.commentList else null
    }

    override fun getMediaUrl(): String? {
        return if (contents != null && contents!!.size == 1) contents!!.first()?.getMediaUrl() else null
    }

    override fun getDao(): BaseDao<ExploreEventEntity> {
        return FetLifeApplication.instance.fetLifeDatabase.exploreEventDao()
    }


}