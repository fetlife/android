package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.bitlove.fetlife.model.dataobject.entity.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.UserEntity
import com.bitlove.fetlife.model.dataobject.entity.reference.MemberRef
import com.bitlove.fetlife.model.dataobject.wrapper.Member
import com.bitlove.fetlife.model.dataobject.wrapper.User

@Dao
abstract class UserDao : BaseDao<UserEntity> {

    @Query("SELECT * FROM users ORDER BY lastLoggedIn DESC LIMIT 1")
    abstract fun getLastLoggedInUser(): LiveData<List<User>>

    @Query("DELETE FROM users WHERE userName = :userName")
    abstract fun delete(userName: String)

    @Query("DELETE FROM users WHERE rememberUser = :rememberUser AND userName = :receiveNotifications")
    abstract fun clean(rememberUser: Boolean = false, receiveNotifications: Boolean = false)

}