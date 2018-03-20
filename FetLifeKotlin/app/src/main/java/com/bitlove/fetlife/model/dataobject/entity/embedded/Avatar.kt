package com.bitlove.fetlife.model.dataobject.entity.embedded

import android.arch.persistence.room.Embedded

data class Avatar(var id: String = "",
                  @Embedded(prefix = "variants_")
                     var variants: AvatarVariants?)