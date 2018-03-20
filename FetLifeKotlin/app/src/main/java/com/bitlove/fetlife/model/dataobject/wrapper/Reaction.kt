package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.viewmodel.generic.AvatarViewDataHolder
import com.bitlove.fetlife.viewmodel.generic.ReactionViewDataHolder

class Reaction() : ReactionViewDataHolder(), SyncObject<ReactionEntity> {

    enum class TYPE {COMMENT, LOVE}

    constructor(reactionEntity: ReactionEntity? = null) : this() {
        this.reactionEntity = reactionEntity
    }

    @Embedded var reactionEntity: ReactionEntity? = null

    @Relation(parentColumn = "memberId", entityColumn = "dbId", entity = MemberEntity::class)
    var creatorSingleItemList: List<MemberEntity>? = null
//    @Relation(parentColumn = "contentId", entityColumn = "dbId", entity = ContentEntity::class)
//    var contentSingleItemList: List<ContentEntity>? = null

    override fun getLocalId(): String? {
        return reactionEntity?.dbId
    }

    override fun getRemoteId(): String? {
        return reactionEntity?.networkId
    }

    override fun getAvatar(): AvatarViewDataHolder? {
        return Member(creatorSingleItemList?.firstOrNull())
    }

    override fun getText(): String? {
        return reactionEntity?.body
    }

    override fun getEntity(): ReactionEntity? {
        return reactionEntity
    }

    override fun getDao(): BaseDao<ReactionEntity> {
        return getDataBase().reactionDao()
    }

}