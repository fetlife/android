package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.entity.user.UserEntity
import com.bitlove.fetlife.model.dataobject.wrapper.User

@Dao
abstract class UserDao : BaseDao<UserEntity> {

    @Query("SELECT * FROM users ORDER BY lastLoggedIn DESC LIMIT 1")
    abstract fun getLastLoggedInUser(): LiveData<List<User>>

    @Query("DELETE FROM users WHERE dbId = :dbId")
    abstract fun delete(dbId: String)

    @Query("DELETE FROM users WHERE rememberUser = :rememberUser AND receiveNotifications = :receiveNotifications")
    abstract fun clean(rememberUser: Boolean = false, receiveNotifications: Boolean = false)

}