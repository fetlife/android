package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.bitlove.fetlife.model.dataobject.base.Comment
import com.bitlove.fetlife.model.dataobject.base.Conversation
import com.bitlove.fetlife.model.dataobject.joined.ConversationWithMessages

@Dao
abstract class ConversationWithMessagesDao : ConversationDao {

    @Query("SELECT * FROM Conversation ORDER BY updatedAt DESC")
    abstract fun getAllWithMessages(): LiveData<List<ConversationWithMessages>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLastMessage(obj: Comment)

    @Transaction
    open fun insertWithLastMessage(vararg conversations: Conversation) {
        insert(*conversations)
        for (conversation in conversations) {
            val lastMessage = conversation.lastMessage
            if (lastMessage != null) {
                insertLastMessage(lastMessage.asComment(conversation.conversationAppId))
            }
        }
    }

}