package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.logic.dataholder.AvatarViewDataHolder
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.dataholder.ReactionViewDataHolder
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreEventEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ExploreStoryEntity
import com.bitlove.fetlife.model.dataobject.entity.content.FavoriteEntity
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.parseServerTime
import org.ocpsoft.prettytime.PrettyTime
import java.util.*


class ExploreStory: CardViewDataHolder(), SyncObject<ExploreStoryEntity>, Favoritable {

    enum class TYPE {FRESH_AND_PERVY, KINKY_AND_POPULAR, STUFF_YOU_LOVE, EXPLORE_FRIENDS}

    companion object {
        const val VALUE_ACTION_LIKE_CREATED = "like_created"
        const val VALUE_ACTION_WRITING_CREATED = "post_created"
        const val VALUE_ACTION_PICTURE_CREATED = "picture_created"
    }

    @Embedded
    lateinit var exploreStoryEntity: ExploreStoryEntity

    @Relation(parentColumn = "dbId", entityColumn = "storyId", entity = ExploreEventEntity::class)
    var exploreEvents: List<ExploreEvent>? = null

    @Ignore private var generatedTitle : String? = null
    @Ignore private var generatedSubTitle : String? = null
    @Ignore private var generatedChildrenTitle : String? = null

    override fun getAvatarTitle(): String? {
        if (exploreEvents == null) {
            return null
        }

        if (generatedTitle == null) {
            generatedTitle = generateTitle(false)
        }

        return generatedTitle
    }

    override fun getAvatarSubTitle(): String? {
        if (exploreEvents == null) {
            return null
        }

        if (generatedSubTitle == null) {
            generatedSubTitle = generateSubTitle()
        }

        return generatedSubTitle
    }

    private fun generateSubTitle() : String {
        val time = exploreStoryEntity.createdAt?.parseServerTime()?:return ""
        val p = PrettyTime(Locale.getDefault())
        return p.format(Date(time))
    }

    private fun generateTitle(childrenTitle: Boolean) : String {
        val resources = FetLifeApplication.instance.resources

        val nickname = getAvatar()?.getAvatarName()

        var targetString = when(exploreEvents!!.firstOrNull()?.getChild()?.getType()) {
            Content.TYPE.PICTURE.toString() -> if (exploreEvents!!.size > 1 ) resources.getString(R.string.explore_target_picture_plural) else resources.getString(R.string.explore_target_picture_singular)
            Content.TYPE.WRITING.toString() -> if (exploreEvents!!.size > 1 ) resources.getString(R.string.explore_target_writing_plural) else resources.getString(R.string.explore_target_writing_singular)
            else -> ""
        }

        val pointerString = if (exploreEvents!!.size == 1) resources.getString(R.string.explore_pointer_singular) else resources.getString(R.string.explore_pointer_plural)
        val singularNumberString = resources.getString(R.string.explore_number_singular)

        var actionString = when (exploreStoryEntity.action) {
            VALUE_ACTION_LIKE_CREATED ->  resources.getString(R.string.explore_action_like_created)
            VALUE_ACTION_PICTURE_CREATED ->  resources.getString(R.string.explore_action_picture_created)
            VALUE_ACTION_WRITING_CREATED ->  resources.getString(R.string.explore_action_writing_created)
            else -> ""
        }

        return (if(childrenTitle) "$nickname " else "") + actionString + " " + (if(childrenTitle) "$pointerString " else "") + (if (exploreEvents!!.size == 1) singularNumberString else exploreEvents!!.size) + " " + targetString
    }

    override fun getChildrenScreenTitle(): String? {
        if (exploreEvents == null) {
            return null
        }

        if (generatedChildrenTitle == null) {
            generatedChildrenTitle = generateTitle(true)
        }

        return generatedChildrenTitle
    }

    override fun getChildren(): List<CardViewDataHolder>? {
        val children = ArrayList<CardViewDataHolder>()
        return if (exploreEvents == null || exploreEvents!!.size == 1) {
            null
        } else {
            for (exploreEvent in exploreEvents!!) {
                val child = exploreEvent.getChild()
                if (child != null) children.add(child)
            }
            children
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

    override fun isFavorite(): Boolean? {
        return if (exploreEvents?.size == 1) {
            exploreEvents!!.first().isFavorite()
        } else {
            null
        }
    }

    override fun getFavoriteEntity(): FavoriteEntity? {
        return if (exploreEvents?.size == 1) {
            exploreEvents!!.first().getFavoriteEntity()
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

    override fun getUrl(): String? {
        return if (exploreEvents?.size == 1) {
            exploreEvents!!.first().getUrl()
        } else {
            null
        }
    }

    override fun getTitle(): String? {
        return if (exploreEvents?.size == 1) {
            exploreEvents!!.first().getTitle()
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

    override fun getRemoteOrder(): Int? {
        return exploreStoryEntity?.serverOrder
    }

    override fun getType(): String? {
        return exploreStoryEntity.type
    }

    override fun getChild(): CardViewDataHolder? {
        return if (exploreEvents?.size == 1) {
            exploreEvents!!.first().getChild()
        } else {
            null
        }
    }

    override fun getCreatedAt(): String? {
        return if (exploreEvents?.isEmpty() == false) {
            exploreEvents!!.first().getCreatedAt()
        } else {
            null
        }
    }

    override fun isPlaceholder(): Boolean {
        //TODO: remove workaround
        return !isSupported()
    }

    private fun isSupported(): Boolean {
        return if (exploreEvents?.isEmpty() != false) {
            false
        } else {
            return when (exploreStoryEntity.action) {
                "post_created",
                "picture_created",
                "like_created" -> {
                    if (exploreEvents!![0]?.getEntity().contentId != null) {
                        true
                    } else if (exploreEvents!!.size > 1 ){
                        false
                    } else {
                        exploreEvents!![0]?.getEntity().contentId != null
                    }
                }
                else -> false
            }
        }
    }

    override fun getEntity(): ExploreStoryEntity {
        return exploreStoryEntity
    }

    override fun getDao(contentDb: FetLifeContentDatabase): BaseDao<ExploreStoryEntity> {
        return contentDb.exploreStoryDao()
    }

}