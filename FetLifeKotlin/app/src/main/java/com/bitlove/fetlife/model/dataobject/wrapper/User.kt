package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import com.bitlove.fetlife.model.dataobject.entity.UserEntity
import com.bitlove.fetlife.model.db.dao.ContentDao
import com.bitlove.fetlife.viewmodel.generic.AvatarViewDataHolder
import com.bitlove.fetlife.viewmodel.generic.CardViewDataHolder
import com.bitlove.fetlife.viewmodel.generic.ReactionViewDataHolder

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