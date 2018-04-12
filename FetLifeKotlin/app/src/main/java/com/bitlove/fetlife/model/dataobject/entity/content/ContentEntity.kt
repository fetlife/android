package com.bitlove.fetlife.model.dataobject.entity.content

import android.arch.persistence.room.*
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.model.dataobject.entity.reference.PictureVariants
import com.bitlove.fetlife.model.dataobject.entity.reference.ReactionRef
import com.google.gson.annotations.SerializedName

@Entity(tableName = "contents",
        foreignKeys = arrayOf(
        ForeignKey(
                entity = MemberEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("memberId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT)
))
data class ContentEntity(@SerializedName("id") var networkId: String = "",
                         @SerializedName("member_id") var memberId: String? = null,
                         @Ignore @SerializedName("member") var memberRef: MemberRef? = null,
                         @SerializedName("has_new_messages") var hasNewComments: Boolean? = false,
                         @SerializedName("is_loved_by_me") var loved: Boolean? = false,
                         @SerializedName("comment_count") var commentCount: Int? = null,
                         @SerializedName("love_count") var loveCount: Int? = null,
                         @SerializedName("created_at") var createdAt: String? = null,
                         @SerializedName("updated_at") var updatedAt: String? = null,
                         @SerializedName("subject") var subject: String? = null,
                         @SerializedName("body") var body: String? = null,
                         @SerializedName("message_count") var messageCount: Int? = null,
                         @SerializedName("is_archived") var isArchived: Boolean? = false,
                         @Embedded @SerializedName("variants") var pictureVariants: PictureVariants? = null,
                         @Ignore @SerializedName("last_message") var lastMessage: ReactionRef? = null
) : DataEntity {

    var type: String? = null

    @PrimaryKey var dbId: String = ""
        get() {
            return "$type:$networkId"
        }

    var memberRemoteId: String? = null
}