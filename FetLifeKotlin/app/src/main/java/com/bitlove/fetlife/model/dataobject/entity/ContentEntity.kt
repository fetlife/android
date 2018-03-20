package com.bitlove.fetlife.model.dataobject.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.google.gson.annotations.SerializedName

@Entity(tableName = "contents",
        foreignKeys = arrayOf(
        ForeignKey(
                entity = MemberEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("memberId"),
                onDelete = ForeignKey.CASCADE)
))
data class ContentEntity(@SerializedName("id") var networkId: String = "",
                         @SerializedName("member_id") var memberId: String? = null,
                         @Ignore @SerializedName("member") var memberRef: MemberRef? = null,
                         @SerializedName("has_new_messages") var hasNewComments: Boolean? = false,
                         @SerializedName("created_at") var createdAt: String? = "",
                         @SerializedName("updated_at") var updatedAt: String? = "",
                         @SerializedName("subject") var subject: String? = "",
                         @SerializedName("message_count") var commentCount: Int? = 0,
                         @SerializedName("is_archived") var isArchived: Boolean? = false
) : DataEntity {

    var type: String? = null

    @PrimaryKey var dbId: String = ""
        get() {
            return type + ":" + networkId
        }

//    init {
//        if (memberRef != null) {
//            memberId = memberRef?.id
//        }
//    }

}