package com.bitlove.fetlife.model.dataobject.temp

import android.arch.persistence.room.Embedded

data class RefMember(var contentType: String? = "",
                     var nickname: String? = "",
                     var id: String? = "",
                     @Embedded(prefix = "avatar_")  var avatar: Avatar?,
                     var url: String? = "",
                     var metaLine: String? = "")