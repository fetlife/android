package com.bitlove.fetlife.model.dataobject.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "explore_events",
        foreignKeys = arrayOf(
        ForeignKey(
                entity = ExploreStoryEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("storyId"),
                onDelete = ForeignKey.CASCADE),
        ForeignKey(
                entity = ContentEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("contentId"),
                onDelete = ForeignKey.CASCADE),
        ForeignKey(
                entity = ReactionEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("reactionId"),
                onDelete = ForeignKey.CASCADE),
        ForeignKey(
                entity = RelationEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("relationId"),
                onDelete = ForeignKey.CASCADE)
))
data class ExploreEventEntity(
        @SerializedName("story_id") var storyId: String = "",
        @SerializedName("action") var action: String = "",
        @SerializedName("content_id") var contentId: String? = "",
        @SerializedName("reaction_id") var reactionId: String? = "",
        @SerializedName("relation_id") var relationId: String? = "",
        @SerializedName("created_at") var createdAt: String? = "",
        @Ignore @SerializedName("target") var target: Target? = null,
        @Ignore @SerializedName("secondary_target") var secondaryTarget: Target? = null
) : DataEntity {
    @PrimaryKey
    var dbId: String = UUID.randomUUID().toString()
}