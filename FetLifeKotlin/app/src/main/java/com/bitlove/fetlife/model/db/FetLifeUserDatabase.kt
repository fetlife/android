package com.bitlove.fetlife.model.db

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.bitlove.fetlife.model.dataobject.entity.*
import com.bitlove.fetlife.model.db.dao.*

@Database(entities = arrayOf(UserEntity::class), version = 1)
abstract class FetLifeUserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
