package com.bitlove.fetlife.datasource.db.dao

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.bitlove.fetlife.datasource.dataobject.Conversation

@Database(entities = arrayOf(Conversation::class), version = 1)
abstract class FetlifeDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
}
