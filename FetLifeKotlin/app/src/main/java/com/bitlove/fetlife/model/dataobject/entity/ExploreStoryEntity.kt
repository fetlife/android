package com.bitlove.fetlife.model.dataobject.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.util.StringUtil
import android.text.TextUtils
import com.bitlove.fetlife.model.dataobject.LocalObject
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "explore_stories")
data class ExploreStoryEntity (
        @SerializedName("name") var type: String? = "",
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

    private fun generateDbId(): String {
        var dbId = ""
        dbId += type
        dbId += events?.firstOrNull()?.dbId
        return dbId
    }
}