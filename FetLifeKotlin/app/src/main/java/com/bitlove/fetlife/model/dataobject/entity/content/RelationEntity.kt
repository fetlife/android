package com.bitlove.fetlife.model.dataobject.entity.content

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.model.dataobject.wrapper.Group
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "relations",
        foreignKeys = arrayOf(
        ForeignKey(
                entity = MemberEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("relatedMemberId"),
                onDelete = ForeignKey.CASCADE),
        ForeignKey(
                entity = MemberEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("memberId"),
                onDelete = ForeignKey.NO_ACTION,
                onUpdate = ForeignKey.NO_ACTION),
        ForeignKey(
                entity = GroupEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("groupId"),
                onDelete = ForeignKey.NO_ACTION,
                onUpdate = ForeignKey.NO_ACTION),
        ForeignKey(
                entity = EventEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("eventId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT)
))
data class RelationEntity(
        @SerializedName("related_member_id") var relatedMemberId: String = "",
        @SerializedName("relations") var relations: String = "",
        @SerializedName("member_id") var memberId: String = "",
        @Ignore @SerializedName("member") var memberRef: MemberRef? = null,
        @SerializedName("group_id") var groupId: String? = null,
        @Ignore @SerializedName("group") var groupRef: GroupEntity? = null,
        @SerializedName("event_id") var eventId: String? = null,
        @SerializedName("last_visited_at") var visitedAt: String? = "",
        @SerializedName("created_at") var createdAt: String? = ""
) : DataEntity {
    @PrimaryKey
    var dbId: String = UUID.randomUUID().toString()

    var serverOrder = 0

    companion object {
        const val RELATION_SEPARATOR = ","
    }

    enum class Relation {
        GROUP_MEMBER
    }
}