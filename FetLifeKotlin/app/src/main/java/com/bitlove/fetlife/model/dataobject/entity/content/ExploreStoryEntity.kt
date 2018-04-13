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

    var serverOrder : Int = 0

    @PrimaryKey
    var dbId: String = ""
        get() {
            if (TextUtils.isEmpty(field)) {
                field = generateDbId()
            }
            return field
        }

    var type: String? = null

    private fun generateDbId(): String {
        var dbId = ""
        dbId += type
        if (events != null) {
            for (event in events!!) {
                dbId += event.remoteId
            }
        }
        return dbId
    }
}