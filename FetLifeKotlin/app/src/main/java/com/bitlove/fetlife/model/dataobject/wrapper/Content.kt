package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import android.text.TextUtils
import com.bitlove.fetlife.model.dataobject.entity.content.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.content.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ReactionEntity
import com.bitlove.fetlife.model.db.dao.ContentDao
import com.bitlove.fetlife.logic.dataholder.AvatarViewDataHolder
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.FavoriteEntity
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.parseServerTime
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class Content : CardViewDataHolder(), SyncObject<ContentEntity>, Favoritable {

    enum class TYPE {CONVERSATION, PICTURE, WRITING}

    @Embedded lateinit var contentEntity: ContentEntity

    @Relation(parentColumn = "memberId", entityColumn = "dbId", entity = MemberEntity::class)
    var ownerSingleItemList: List<MemberEntity>? = null

    @Relation(parentColumn = "dbId", entityColumn = "contentId", entity = ReactionEntity::class)
    var reactions: List<Reaction>? = null

    @Relation(parentColumn = "dbId", entityColumn = "contentId", entity = FavoriteEntity::class)
    var favoriteSingleItem: List<FavoriteEntity>? = null

    @Ignore private var commentList: List<Reaction>? = null
    @Ignore private var loveList: List<Reaction>? = null

    override fun getAvatarTitle(): String? {
        return if (contentEntity?.subject?.isEmpty() == false) {
            contentEntity?.subject
        } else {
            getPrettyCreatedAt()
        }
    }

    override fun getAvatarSubTitle(): String? {
        return if (contentEntity?.subject?.isEmpty() == false) {
            getPrettyCreatedAt()
        } else {
            ""
        }
    }

    private fun getPrettyCreatedAt() : String {
        val time = contentEntity.createdAt?.parseServerTime()?:return ""
        val p = PrettyTime(Locale.getDefault())
        return p.format(Date(time))
    }

    override fun getTitle(): String? {
        return contentEntity.title
    }

    override fun displayComments(): Boolean? {
        return when(contentEntity?.type) {
            TYPE.CONVERSATION.toString() -> true
            else -> false
        }
    }

    override fun getSupportingText(): String? {
        return contentEntity?.body
    }

    override fun getLocalId(): String? {
        return contentEntity?.dbId
    }

    override fun getRemoteId(): String? {
        return contentEntity?.networkId
    }

    override fun getType(): String? {
        return contentEntity?.type
    }

    override fun getServerType(): String {
        return try {
            val type = TYPE.valueOf(getType()!!)
            when (type) {
                TYPE.PICTURE -> "pictures"
                TYPE.CONVERSATION -> "conversation"
                TYPE.WRITING -> "writings"
            }
        } catch (t: Throwable) {
            throw IllegalArgumentException(t)
        }
    }

    override fun getAvatar(): AvatarViewDataHolder? {
        val memberEntity = ownerSingleItemList?.firstOrNull() ?: return null
        return Member(memberEntity)
    }

    override fun getThumbUrl(): String? {
        return contentEntity?.pictureVariants?.medium?.url
    }

    override fun getMediaUrl(): String? {
        return contentEntity?.pictureVariants?.huge?.url
    }

    override fun getMediaAspectRatio(): Float? {
        val picVariant = contentEntity?.pictureVariants?.huge
        return if (picVariant != null) {
            picVariant.width.toFloat() / picVariant.height.toFloat()
        } else {
            null
        }
    }

    override fun getUrl(): String? {
        return contentEntity?.url
    }

    override fun isDeletable(): Boolean? {
        return when(contentEntity?.type) {
            TYPE.CONVERSATION.toString() -> true
            else -> false
        }
    }

    override fun getCreatedAt(): String? {
        return contentEntity?.createdAt
    }

    override fun hasNewComment(): Boolean? {
        return contentEntity?.hasNewComments
    }

    override fun getCommentCountText(): String? {
        return when(contentEntity?.type) {
            TYPE.CONVERSATION.toString() -> contentEntity?.messageCount?.toString()
            else -> contentEntity?.commentCount?.toString()
        }
    }

    override fun getComments(): List<Reaction> {
        if (commentList == null) {
            populateComments()
        }
        return commentList!!
    }

    override fun getLoves(): List<Reaction> {
        if (loveList == null) {
            populateLoves()
        }
        return loveList!!
    }

    override fun isLoved(): Boolean? {
        return contentEntity?.loved
    }

    override fun isFavorite(): Boolean? {
        return favoriteSingleItem?.firstOrNull() != null
    }

    override fun getFavoriteEntity(): FavoriteEntity? {
        return favoriteSingleItem?.firstOrNull()
    }

    override fun getLoveCount(): String? {
        return contentEntity?.loveCount?.toString()
    }

    private fun populateComments() {
        var reactions = reactions ?: return
        commentList = reactions.filter { it.reactionEntity?.type == Reaction.TYPE.COMMENT.toString() }.sortedBy { it.getEntity().createdAt }
    }

    private fun populateLoves() {
        var reactions = reactions ?: return
        loveList = reactions.filter { it.reactionEntity?.type == Reaction.TYPE.LOVE.toString() }
    }

    override fun getEntity(): ContentEntity {
        return contentEntity
    }

    override fun getDao(contentDb: FetLifeContentDatabase): ContentDao {
        return contentDb.contentDao()
    }

}