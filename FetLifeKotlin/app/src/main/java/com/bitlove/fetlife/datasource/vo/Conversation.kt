package com.bitlove.fetlife.datasource.vo

import com.google.gson.annotations.SerializedName

data class Conversation(@SerializedName("has_new_messages")
                        val hasNewMessages: Boolean = false,
                        @SerializedName("is_archived")
                        val isArchived: Boolean = false,
                        @SerializedName("updated_at")
                        val updatedAt: String = "",
                        val subject: String = "",
                        val member: Member?,
                        @SerializedName("created_at")
                        val createdAt: String = "",
                        @SerializedName("last_message")
                        val lastMessage: Message?,
                        val id: String = "",
                        @SerializedName("message_count")
                        val messageCount: Int = 0)