package com.bitlove.fetlife.model.dataobject.entity

import android.arch.persistence.room.*
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.model.dataobject.entity.reference.PictureVariants
import com.bitlove.fetlife.model.dataobject.entity.reference.ReactionRef
import com.google.gson.annotations.SerializedName
import org.jetbrains.anko.db.FOREIGN_KEY

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
                         @SerializedName("created_at") var createdAt: String? = "",
                         @SerializedName("updated_at") var updatedAt: String? = "",
                         @SerializedName("subject") var subject: String? = "",
                         @SerializedName("body") var body: String? = "",
                         @SerializedName("message_count") var commentCount: Int? = 0,
                         @SerializedName("is_archived") var isArchived: Boolean? = false,
                         @Embedded @SerializedName("variants") var pictureVariants: PictureVariants? = null,
                         @Ignore @SerializedName("last_message") var lastMessage: ReactionRef? = null
) : DataEntity {

    var type: String? = null

    @PrimaryKey var dbId: String = ""
        get() {
            return type + ":" + networkId
        }

}