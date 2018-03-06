package com.bitlove.fetlife.model.network.networkobject

import com.google.gson.annotations.SerializedName

data class AuthBody(
        @SerializedName("username") var username: String?,
        @SerializedName("password") var password: String?,
        @SerializedName("grant_type") var grantType: String = "password")