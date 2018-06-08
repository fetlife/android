package com.bitlove.fetlife.model.dataobject.entity.content

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils
import com.bitlove.fetlife.model.dataobject.entity.embedded.Avatar
import com.google.gson.annotations.SerializedName

@Entity(tableName = "members")
class MemberEntity(@SerializedName("id") var networkId: String = "",
                   @SerializedName("nickname") var nickname: String?  = "",
                   @SerializedName("meta_line") var metaInfo: String? = null,
                   @SerializedName("url") var url: String? = null,
                   @SerializedName("is_supporter") var isSupporter: Boolean? = null,
                   @Embedded @SerializedName("avatar") var avatar: Avatar? = null) : DataEntity {

    @PrimaryKey
    var dbId: String = ""
        get() {
            return if (TextUtils.isEmpty(field)) this::class.qualifiedName + ":" + networkId else field
        }
}