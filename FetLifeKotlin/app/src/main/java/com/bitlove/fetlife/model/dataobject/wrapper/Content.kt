package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import com.bitlove.fetlife.model.db.dao.ContentDao
import com.bitlove.fetlife.viewmodel.generic.AvatarViewDataHolder
import com.bitlove.fetlife.viewmodel.generic.CardViewDataHolder
import com.bitlove.fetlife.viewmodel.generic.ReactionViewDataHolder

class Content : CardViewDataHolder(), SyncObject<ContentEntity> {

    enum class TYPE {CONVERSATION, PICTURE}

    @Embedded var contentEntity: ContentEntity? = null

    @Relation(parentColumn = "memberId", entityColumn = "dbId", entity = MemberEntity::class)
    var ownerSingleItemList: List<MemberEntity>? = null

    @Relation(parentColumn = "dbId", entityColumn = "contentId", entity = ReactionEntity::class)
    var reactions: List<ReactionEntity>? = null

    @Ignore var commentList: List<Reaction>? = null
    @Ignore var loveList: List<Reaction>? = null

    override fun getLocalId(): String? {
        return contentEntity?.dbId
    }

    override fun getRemoteId(): String? {
        return contentEntity?.networkId
    }

    override fun getAvatar(): AvatarViewDataHolder? {
        return Member(ownerSingleItemList?.firstOrNull())
    }

    override fun getComments(): List<ReactionViewDataHolder> {
        if (commentList == null) {
            populateComments()
        }
        return commentList!!
    }

    override fun getLoves(): List<ReactionViewDataHolder> {
        if (loveList == null) {
            populateLoves()
        }
        return loveList!!
    }

    open fun populateComments() {
        val commentArrayList = ArrayList<Reaction>()
        var reactions = reactions ?: return
        reactions
                .filter { it.type == Reaction.TYPE.COMMENT.toString() }
                .mapTo(commentArrayList) { Reaction(it) }
        commentList = commentArrayList
    }

    open fun populateLoves() {
        val loveArrayList = ArrayList<Reaction>()
        var reactions = reactions ?: return
        reactions
                .filter { it.type == Reaction.TYPE.LOVE.toString() }
                .mapTo(loveArrayList) { Reaction(it) }
        loveList = loveArrayList
    }

    override fun getEntity(): ContentEntity? {
        return contentEntity
    }

    override fun getDao(): ContentDao {
        return getDataBase().contentDao()
    }

}