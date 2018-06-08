package com.bitlove.fetlife.model.dataobject.entity.content

import android.arch.persistence.room.*
import android.text.TextUtils
import com.bitlove.fetlife.hash
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.parseServerTime
import com.google.gson.annotations.SerializedName

@Entity(tableName = "reactions",
        foreignKeys = arrayOf(
        ForeignKey(
                entity = ContentEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("contentId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT),
        ForeignKey(
                entity = MemberEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("memberId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT)
))
data class ReactionEntity(
        @SerializedName("id") var networkId: String = "",
        @SerializedName("group_id") var groupId: String? = "",
        @SerializedName("content_id") var contentId: String? = "",
        @SerializedName("member_id") var memberId: String? = "",
        @Ignore @SerializedName("member") var memberRef: MemberRef? = null,
        @SerializedName("body") var body: String? = "",
        @SerializedName("is_new") var isNew: Boolean? = false,
        @SerializedName("created_at") var createdAt: String? = ""
) : DataEntity {
    var type: String? = null

    @PrimaryKey var dbId: String = ""
        get() {
            if (TextUtils.isEmpty(field)) {
                field = "$type:${createdAt?.hash()}:${body?.hash()}"
            }
            return field
        }

    var createdAtTime: Long = 0L
        get() {
            if (field == 0L && !TextUtils.isEmpty(createdAt)) {
                field = createdAt!!.parseServerTime()
            }
            return field
        }

    var serverOrder = 0
}
