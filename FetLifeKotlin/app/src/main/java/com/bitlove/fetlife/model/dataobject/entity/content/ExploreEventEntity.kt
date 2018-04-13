package com.bitlove.fetlife.model.dataobject.entity.content

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.model.dataobject.entity.reference.TargetRef
import com.google.gson.annotations.SerializedName

@Entity(tableName = "explore_events",
        foreignKeys = arrayOf(
        ForeignKey(
                entity = ExploreStoryEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("storyId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT),
        ForeignKey(
                entity = MemberEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("ownerId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT),
        ForeignKey(
                entity = MemberEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("memberId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT),
        ForeignKey(
                entity = ContentEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("contentId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT),
        ForeignKey(
                entity = ReactionEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("reactionId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT),
        ForeignKey(
                entity = RelationEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("relationId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT)
))
data class ExploreEventEntity(
        @SerializedName("story_id") var storyId: String = "",
        @SerializedName("owner_Id") var ownerId: String = "",
        @SerializedName("action") var action: String = "",
        @SerializedName("member_id") var memberId: String? = null,
        @SerializedName("content_id") var contentId: String? = null,
        @SerializedName("reaction_id") var reactionId: String? = null,
        @SerializedName("relation_id") var relationId: String? = null,
        @SerializedName("created_at") var createdAt: String? = null,
        @Ignore @SerializedName("member") var memberRef: MemberRef? = null,
        @Ignore @SerializedName("target") var target: TargetRef? = null,
        @Ignore @SerializedName("secondary_target") var secondaryTarget: TargetRef? = null
) : DataEntity {

    @PrimaryKey
    var dbId: String = ""
        get() {
            if (TextUtils.isEmpty(field)) {
                field = generateDbId()
            }
            return field
        }

    var remoteId: String = ""
        get() {
            if (TextUtils.isEmpty(field)) {
                field = generateRemoteId()
            }
            return field
        }

    private fun generateDbId(): String {
        var dbId = ""
        dbId += storyId
        dbId += remoteId
        return dbId
    }

    private fun generateRemoteId(): String {
        var dbId = ""
        dbId += memberRef?.id
        dbId += target?.id
        dbId += secondaryTarget?.id
        return dbId
    }
}