package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ReactionEntity
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.logic.dataholder.AvatarViewDataHolder
import com.bitlove.fetlife.logic.dataholder.ReactionViewDataHolder
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.parseServerTime
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class Reaction() : ReactionViewDataHolder(), SyncObject<ReactionEntity> {

    enum class TYPE {COMMENT, LOVE}

    constructor(reactionEntity: ReactionEntity) : this() {
        this.reactionEntity = reactionEntity
    }

    @Embedded lateinit var reactionEntity: ReactionEntity

    @Relation(parentColumn = "memberId", entityColumn = "dbId", entity = MemberEntity::class)
    var creatorSingleItemList: List<MemberEntity>? = null
//    @Relation(parentColumn = "groupId", entityColumn = "dbId", entity = ContentEntity::class)
//    var contentSingleItemList: List<ContentEntity>? = null

    override fun getLocalId(): String? {
        return reactionEntity?.dbId
    }

    override fun getRemoteId(): String? {
        return reactionEntity?.networkId
    }

    override fun getType(): String? {
        return reactionEntity?.type
    }

    override fun getAvatar(): AvatarViewDataHolder? {
        val member = creatorSingleItemList?.firstOrNull() ?: return null
        return Member(member)
    }

    override fun getText(): String? {
        return reactionEntity?.body
    }

    override fun getTime(): String? {
        val time = reactionEntity.createdAt?.parseServerTime()?:return ""
        val p = PrettyTime(Locale.getDefault())
        return p.format(Date(time))
    }

    override fun getEntity(): ReactionEntity {
        return reactionEntity
    }

    override fun getDao(contentDb: FetLifeContentDatabase): BaseDao<ReactionEntity> {
        return contentDb.reactionDao()
    }

}