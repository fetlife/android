package com.bitlove.fetlife.model.dataobject

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.bitlove.fetlife.viewmodel.generic.CardViewDataHolder
import com.google.gson.annotations.SerializedName

@Entity
data class Conversation(
        @SerializedName("id") var serverId: String = "",
        @SerializedName("has_new_messages") var hasNewMessages: Boolean = false,
        @SerializedName("is_archived") var isArchived: Boolean = false,
        @SerializedName("updated_at") var updatedAt: String = "",
        @SerializedName("created_at") var createdAt: String = "",
        var subject: String = "",
        @Embedded(prefix = "member_") var member: RefMember?,
        @SerializedName("last_message") @Embedded(prefix = "message_") var lastMessage: RefMessage?,
        @SerializedName("message_count") var messageCount: Int = 0

): DataObject {
    //TODO clean this App
    @PrimaryKey var appId: String = ""
        get():String {
            return Conversation::class.simpleName + ":" + serverId
        }
}