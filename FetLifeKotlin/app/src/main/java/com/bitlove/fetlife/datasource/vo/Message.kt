package com.bitlove.fetlife.datasource.vo

import com.google.gson.annotations.SerializedName

data class Message(@SerializedName("is_new")
                       val isNew: Boolean = false,
                   val member: Member?,
                   @SerializedName("created_at")
                       val createdAt: String = "",
                       //@SerializedName("body_entities")
                       //val bodyEntities: BodyEntities?,
                   val id: String = "",
                   val body: String = "")