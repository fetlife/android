package com.bitlove.fetlife.model.dataobject.entity.reference

import com.google.gson.annotations.SerializedName

data class Message(@SerializedName("id") var id: String = "",
                   @SerializedName("memberRef")  var memberRef: MemberRef?,
                   @SerializedName("body") var body: String = "",
                   @SerializedName("is_new") var isNew: Boolean = false,
                   @SerializedName("created_at") var createdAt: String = "")