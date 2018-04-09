package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import com.bitlove.fetlife.model.dataobject.entity.UserEntity

class User {

    @Embedded lateinit var userEntity: UserEntity

    fun getUserName(): String {
        return userEntity.username
    }

    fun getAccessToken(): String? {
        return userEntity.accessToken
    }

    fun getRefreshToken(): String? {
        return userEntity.refreshToken
    }

    fun rememberUser(): Boolean {
        return userEntity.rememberUser == true
    }

    fun getLastLoggedIn(): Long {
        return userEntity.lastLoggedIn
    }

    fun receiveNotifications(): Boolean {
        return userEntity.receiveNotifications == true
    }

}