package com.bitlove.fetlife.model.dataobject.entity.content

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.util.StringUtil
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils

@Entity(tableName = "events")
data class EventEntity(@SerializedName("id") var networkId: String = "") : DataEntity {
    @PrimaryKey
    var dbId: String = ""
        get() {
            return if (StringUtils.isEmpty(field)) this::class.qualifiedName + ":" + networkId else field
        }
}