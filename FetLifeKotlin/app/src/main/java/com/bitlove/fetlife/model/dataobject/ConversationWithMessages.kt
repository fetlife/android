package com.bitlove.fetlife.model.dataobject

import android.arch.persistence.room.*
import com.bitlove.fetlife.viewmodel.generic.CardViewDataHolder
import com.google.gson.annotations.SerializedName

class ConversationWithMessages : CardViewDataHolder() {

    @Embedded var conversation: Conversation? = null

    @Relation(parentColumn = "appId", entityColumn = "commentForId")
    var messages: List<Comment>? = null

    override fun getDataId(): String {
        return conversation!!.appId
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
}