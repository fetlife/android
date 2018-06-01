package com.bitlove.fetlife.model.dataobject.entity.reference

import com.bitlove.fetlife.model.dataobject.entity.content.ContentEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.google.gson.annotations.SerializedName

data class RelationRef(
        @SerializedName("id") var id: String = "",
        @SerializedName("created_at") var createdAt: String = ""
) {
}