package com.bitlove.fetlife.datasource.dataobject

import android.arch.persistence.room.Embedded
import com.google.gson.annotations.SerializedName

data class RefMessage(  var id: String = "",
                        var body: String = "",
                        @Embedded(prefix = "member_")  var member: RefMember?,
                        @SerializedName("is_new") var isNew: Boolean = false,
                        @SerializedName("created_at") var createdAt: String = "")