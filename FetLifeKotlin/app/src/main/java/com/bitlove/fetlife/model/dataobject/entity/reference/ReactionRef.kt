package com.bitlove.fetlife.model.dataobject.entity.reference

import com.bitlove.fetlife.model.dataobject.entity.content.ReactionEntity
import com.google.gson.annotations.SerializedName

data class ReactionRef(@SerializedName("id") var id: String = "",
                       @SerializedName("member")  var memberRef: MemberRef?,
                       @SerializedName("body") var body: String = "",
                       @SerializedName("is_new") var isNew: Boolean = false,
                       @SerializedName("created_at") var createdAt: String = "") {

    fun asEntity(): ReactionEntity {
        val entity = ReactionEntity()
        entity.body = body
        entity.networkId = id
        entity.isNew = isNew
        entity.createdAt = createdAt
        return entity
    }
}