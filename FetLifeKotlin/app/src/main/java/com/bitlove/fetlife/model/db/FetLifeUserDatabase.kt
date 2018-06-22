package com.bitlove.fetlife.model.db

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.bitlove.fetlife.model.dataobject.entity.technical.JobProgressEntity
import com.bitlove.fetlife.model.dataobject.entity.user.UserEntity
import com.bitlove.fetlife.model.db.dao.*

@Database(entities = arrayOf(UserEntity::class, JobProgressEntity::class), version = 5)
abstract class FetLifeUserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun jobProgressDao(): JobProgressDao
}
