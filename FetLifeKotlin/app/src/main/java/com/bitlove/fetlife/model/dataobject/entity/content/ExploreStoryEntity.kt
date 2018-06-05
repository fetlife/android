package com.bitlove.fetlife.model.dataobject.entity.content

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils
import com.google.gson.annotations.SerializedName

@Entity(tableName = "explore_stories")
data class ExploreStoryEntity (
        @SerializedName("name") var action: String? = "",
        @Ignore @SerializedName("events") var events: List<ExploreEventEntity>? = null
) : DataEntity {

    @PrimaryKey
    var dbId: String = ""
        get() {
            return type + createdAt
        }
    var type: String? = null
    var createdAt: String? = ""

    var serverOrder: Int = 0
    var supported: Boolean = true
}