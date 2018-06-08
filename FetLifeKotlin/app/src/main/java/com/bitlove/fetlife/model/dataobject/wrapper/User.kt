package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import com.bitlove.fetlife.model.dataobject.entity.content.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.user.UserEntity

class
User {

    @Embedded lateinit var userEntity: UserEntity
    @Ignore lateinit var memberEntity: MemberEntity

    fun getLocalId(): String {
        return userEntity.dbId
    }

    fun getNetworkId(): String {
        return memberEntity.networkId
    }

    fun getUserName(): String? {
        return userEntity.userName
    }

    fun getNickname() : String? {
        return memberEntity?.nickname
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