package com.bitlove.fetlife.model.dataobject.embedded

import android.arch.persistence.room.Embedded

data class EmbAvatar(var id: String = "",
                     @Embedded(prefix = "variants_")
                     var variants: EmbVariants?)