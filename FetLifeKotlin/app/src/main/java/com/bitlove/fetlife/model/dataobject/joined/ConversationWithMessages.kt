package com.bitlove.fetlife.model.dataobject.joined

import android.arch.persistence.room.*
import com.bitlove.fetlife.model.dataobject.DataObject
import com.bitlove.fetlife.model.dataobject.base.Comment
import com.bitlove.fetlife.model.dataobject.base.Conversation
import com.bitlove.fetlife.viewmodel.generic.CardViewDataHolder

class ConversationWithMessages : DataObject, CardViewDataHolder() {

    @Embedded var conversation: Conversation? = null

    @Relation(parentColumn = "conversationAppId", entityColumn = "parentId")
    var messages: List<Comment>? = null

    override fun getAppId(): String {
        return conversation!!.getAppId()
    }

    override fun getServerId(): String {
        return conversation!!.getServerId()
    }

    override fun getAvatarUrl(): String? {
        return conversation?.member?.avatar?.variants?.medium
    }

    override fun getAvatarName(): String? {
        return conversation?.member?.nickname
    }

    override fun getAvatarMeta(): String? {
        return conversation?.member?.metaInfo
    }

    override fun getAvatarSubline(): String? {
        return conversation?.subject
    }

    override fun getComments(): List<Comment>? {
        return messages
    }

    override fun hasNewComment(): Boolean? {
        return conversation?.hasNewMessages
    }
}