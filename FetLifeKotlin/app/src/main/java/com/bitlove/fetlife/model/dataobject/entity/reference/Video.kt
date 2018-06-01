package com.bitlove.fetlife.model.dataobject.entity.reference

import com.bitlove.fetlife.model.dataobject.entity.content.ContentEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.google.gson.annotations.SerializedName

data class Video(
        @SerializedName("id") var id: String = "",
        @SerializedName("created_at") var createdAt: String = ""
) {
    fun asEntity(): ContentEntity {
        val contentEntity = ContentEntity()
        contentEntity.networkId = id
        contentEntity.type = Content.TYPE.WRITING.toString()
        contentEntity.createdAt = createdAt
        contentEntity.hasNewComments = false
        return contentEntity
    }
}