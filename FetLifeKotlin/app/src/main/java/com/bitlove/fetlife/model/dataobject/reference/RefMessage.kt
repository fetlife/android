package com.bitlove.fetlife.model.dataobject.reference

import android.arch.persistence.room.Embedded
import com.bitlove.fetlife.model.dataobject.base.Comment
import com.google.gson.annotations.SerializedName

data class RefMessage(var id: String = "",
                      var body: String = "",
                      @Embedded(prefix = "member_")  var member: RefMember?,
                      @SerializedName("is_new") var isNew: Boolean = false,
                      @SerializedName("created_at") var createdAt: String = "") {

    open fun asComment(parentId: String) : Comment {
        return Comment(id,parentId,body,isNew,createdAt,member)
    }

}