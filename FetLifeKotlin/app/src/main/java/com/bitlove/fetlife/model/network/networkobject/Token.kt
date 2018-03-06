package com.bitlove.fetlife.model.network.networkobject

import com.google.gson.annotations.SerializedName

data class Token(
        @SerializedName("access_token") var accessToken: String? = null,
        @SerializedName("refresh_token") var refreshToken: String? = null
)
