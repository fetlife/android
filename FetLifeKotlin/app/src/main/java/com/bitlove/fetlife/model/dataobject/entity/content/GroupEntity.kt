package com.bitlove.fetlife.model.dataobject.entity.content

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils
import com.google.gson.annotations.SerializedName

@Entity(tableName = "groups")
class GroupEntity(@SerializedName("id") var networkId: String = "") : DataEntity {
    @PrimaryKey
    var dbId: String = ""
        get() {
            //TODO modify this to support local creation
            return if (TextUtils.isEmpty(field)) this::class.qualifiedName + ":" + networkId else field
        }
}