package com.bitlove.fetlife.model.dataobject.entity.reference

import com.bitlove.fetlife.model.dataobject.entity.embedded.Avatar
import com.google.gson.annotations.SerializedName

data class MemberRef(@SerializedName("id") var id: String = "",
                     @SerializedName("nickname") var nickname: String?  = "",
                     @SerializedName("meta_line") var metaInfo: String?,
                     @SerializedName("avatar") var avatar: Avatar?)