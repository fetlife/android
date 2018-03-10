package com.bitlove.fetlife.model.db

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.bitlove.fetlife.model.dataobject.base.Comment
import com.bitlove.fetlife.model.dataobject.base.Conversation
import com.bitlove.fetlife.model.dataobject.temp.ExploreStory
import com.bitlove.fetlife.model.db.dao.CommentDao
import com.bitlove.fetlife.model.db.dao.ConversationDao
import com.bitlove.fetlife.model.db.dao.ConversationWithMessagesDao
import com.bitlove.fetlife.model.db.dao.ExploreDao

@Database(entities = arrayOf(Conversation::class, Comment::class, ExploreStory::class), version = 4)
abstract class FetlifeDatabase : RoomDatabase() {
    abstract fun commentDao(): CommentDao
    abstract fun conversationDao(): ConversationDao
    abstract fun conversationWithMessagesDao(): ConversationWithMessagesDao
    abstract fun exploreDao(): ExploreDao
}
