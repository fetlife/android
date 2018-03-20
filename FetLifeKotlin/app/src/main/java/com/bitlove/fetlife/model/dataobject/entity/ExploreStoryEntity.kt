package com.bitlove.fetlife.model.dataobject.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "explore_stories")
data class ExploreStoryEntity(
        @SerializedName("type") var type: String = "",
        @SerializedName("created_at") var createdAt: String? = "",
        @Ignore @SerializedName("events") var eventEntities: List<ExploreEventEntity>? = null
) : DataEntity {
    @PrimaryKey
    var dbId: String = UUID.randomUUID().toString()
}