package com.bitlove.fetlife.model.dataobject.entity.content

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils
import com.google.gson.annotations.SerializedName

@Entity(tableName = "groups")
class GroupEntity(
        @SerializedName("id") var networkId: String = "",
        @SerializedName("name") var name: String = "",
        @SerializedName("created_at") var createdAt: String? = "",
        @SerializedName("updated_at") var updatedAt: String? = "",
        @SerializedName("description") var description: String? = "",
        @SerializedName("rules") var rules: String? = "",
        @SerializedName("member_count") var memberCount: Int? = null,
        @SerializedName("current_member_is_member") var memberOfGroup: Boolean? = null,
        @SerializedName("url") var url: String? = ""
) : DataEntity {
    @PrimaryKey
    var dbId: String = ""
        get() {
            //TODO modify this to support local creation
            return if (TextUtils.isEmpty(field)) this::class.qualifiedName + ":" + networkId else field
        }

    var serverOrder = 0
}