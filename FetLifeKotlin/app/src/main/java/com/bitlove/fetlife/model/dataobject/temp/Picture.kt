package com.bitlove.fetlife.model.dataobject.temp

import android.arch.persistence.room.Embedded
import com.google.gson.annotations.SerializedName

data class Picture(@SerializedName("is_loved_by_me") var isLovedByMe: Boolean = false,
                   @SerializedName("commentCount") var commentCount: Int = 0,
                   @SerializedName("content_type") var contentType: String = "",
                   @Embedded(prefix = "picture_") var member: RefMember,
                   @SerializedName("created_at") var createdAt: String = "",
                   @SerializedName("love_count") var loveCount: Int = 0,
                   var id: String = "",
                   @Embedded var variants: Variants,
                   var body: String = "",
                   var url: String = "")