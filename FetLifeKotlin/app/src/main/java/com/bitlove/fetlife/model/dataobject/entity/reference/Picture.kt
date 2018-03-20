package com.bitlove.fetlife.model.dataobject.entity.reference

import com.google.gson.annotations.SerializedName

data class Picture(
        @SerializedName("id") var id: String = "",
        @SerializedName("memberRef") var memberRef: MemberRef,
        @SerializedName("content_type") var contentType: String = "",
        @SerializedName("created_at") var createdAt: String = "",
        @SerializedName("body") var body: String = "",
        @SerializedName("url") var url: String = "",
        @SerializedName("commentCount") var commentCount: Int = 0,
        @SerializedName("love_count") var loveCount: Int = 0,
        @SerializedName("is_loved_by_me") var isLovedByMe: Boolean = false,
        @SerializedName("variants") var variants: PictureVariants
)