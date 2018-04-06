package com.bitlove.fetlife.model.dataobject.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "users")
class UserEntity(@SerializedName("last_logged_in") var lastLoggedIn: Long = 0L,
                 @PrimaryKey @SerializedName("username") var username: String = "",
                 @SerializedName("access_token") var accessToken: String?  = "",
                 @SerializedName("refresh_token") var refreshToken: String? = null,
                 @SerializedName("remember_user") var rememberUser: Boolean = false,
                 @SerializedName("receive_notifications") var receiveNotifications: Boolean = false) : DataEntity