package com.bitlove.fetlife.model.db

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.bitlove.fetlife.model.dataobject.Comment
import com.bitlove.fetlife.model.dataobject.Conversation
import com.bitlove.fetlife.model.db.dao.ConversationDao
import com.bitlove.fetlife.model.db.dao.ConversationWithMessagesDao

@Database(entities = arrayOf(Conversation::class, Comment::class), version = 2)
abstract class FetlifeDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun conversationWithMessagesDao(): ConversationWithMessagesDao
}
