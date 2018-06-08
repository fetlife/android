package com.bitlove.fetlife.model.dataobject.entity.content

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.model.dataobject.entity.reference.TargetRef
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "favoriteSingleItem",
        foreignKeys = arrayOf(
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
                entity = GroupEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("groupId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT)
        ))
data class FavoriteEntity(
        @PrimaryKey @SerializedName("id") var dbId: String = UUID.randomUUID().toString(),
        @SerializedName("created_at") var createdAt: Long = System.currentTimeMillis(),
        @SerializedName("member_id") var memberId: String? = null,
        @SerializedName("content_id") var contentId: String? = null,
        @SerializedName("group_id") var groupId: String? = null
) : DataEntity