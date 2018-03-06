package com.bitlove.fetlife.model.dataobject

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.bitlove.fetlife.viewmodel.generic.CommentViewDataHolder
import com.google.gson.annotations.SerializedName

@Entity
data class Comment(
        @PrimaryKey var id: String = "",
        var body: String? = "",
        var commentForId: String,
        @SerializedName("is_new") var isNew: Boolean? = false,
        @SerializedName("created_at") var createdAt: String? = "",
        @Embedded(prefix = "member_") var member: RefMember?
) : Message, CommentViewDataHolder() {

    companion object {
        const val TRUNCATED_LENGTH = 224
        const val TRUNCATED_SUFFIX = "…"
    }

    override fun getAvatarUrl(): String? {
        return member?.avatar?.variants?.medium
    }

    override fun getAvatarName(): String? {
        return member?.nickname
    }

    override fun getText(): String? {
        return body
    }

}