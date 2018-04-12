package com.bitlove.fetlife.model.dataobject.entity.content

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils
import com.google.gson.annotations.SerializedName

@Entity(tableName = "explore_stories")
data class ExploreStoryEntity (
        @SerializedName("name") var action: String? = "",
        @SerializedName("created_at") var createdAt: String? = "",
        @Ignore @SerializedName("events") var events: List<ExploreEventEntity>? = null
) : DataEntity {

    @PrimaryKey
    var dbId: String = ""
        get() {
            if (TextUtils.isEmpty(field)) {
                field = generateDbId()
            }
            return field
        }

    var type: String? = null
    var serverOrder: Int? = null

    private fun generateDbId(): String {
        var dbId = ""
        dbId += type
        dbId += serverOrder
        return dbId
    }
}