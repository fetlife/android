package com.bitlove.fetlife.model.dataobject.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "groups")
class GroupEntity(@SerializedName("id") var networkId: String = "") : DataEntity {
    @PrimaryKey
    var dbId: String = ""
        get() {
            return this::class.qualifiedName + ":" + networkId
        }
}