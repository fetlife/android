package com.bitlove.fetlife.model.dataobject

import android.arch.persistence.room.Embedded
import com.google.gson.annotations.SerializedName

data class RefMember(var id: String = "",
                     var nickname: String?  = "",
                     @SerializedName("meta_line") var metaInfo: String?,
                     @Embedded(prefix = "avatar_") var avatar: EmbAvatar?)