package com.bitlove.fetlife.model.dataobject.entity

import android.arch.persistence.room.*
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.google.gson.annotations.SerializedName

@Entity(tableName = "reactions",
        foreignKeys = arrayOf(
        ForeignKey(
                entity = ContentEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("contentId"),
                onDelete = ForeignKey.CASCADE),
        ForeignKey(
                entity = MemberEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("memberId"),
                onDelete = ForeignKey.CASCADE)
))
data class ReactionEntity(
        @SerializedName("id") var networkId: String = "",
        @SerializedName("content_id") var contentId: String? = "",
        @SerializedName("member_id") var memberId: String? = "",
        @Ignore @SerializedName("memberRef") var memberRef: MemberRef? = null,
        @SerializedName("body") var body: String? = "",
        @SerializedName("is_new") var isNew: Boolean? = false,
        @SerializedName("created_at") var createdAt: String? = ""
) : DataEntity {
    var type: String? = null

    @PrimaryKey var dbId: String = ""
        get() {
            return type + ":" + networkId
        }

    init {
        if (memberRef != null) {
            memberId = memberRef?.id
        }
    }
}
