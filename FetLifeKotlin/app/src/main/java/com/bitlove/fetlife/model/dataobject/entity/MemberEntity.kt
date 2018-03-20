package com.bitlove.fetlife.model.dataobject.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.bitlove.fetlife.model.dataobject.entity.embedded.Avatar
import com.google.gson.annotations.SerializedName

@Entity(tableName = "members")
class MemberEntity(@SerializedName("id") var networkId: String = "",
                   @SerializedName("nickname") var nickname: String?  = "",
                   @SerializedName("meta_line") var metaInfo: String? = null,
                   @Embedded @SerializedName("avatar") var avatar: Avatar? = null) : DataEntity {

    @PrimaryKey
    var dbId: String = ""
        get() {
            return this::class.qualifiedName + ":" + networkId
        }
}