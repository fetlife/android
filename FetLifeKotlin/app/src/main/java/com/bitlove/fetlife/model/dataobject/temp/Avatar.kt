package com.bitlove.fetlife.model.dataobject.temp

import android.arch.persistence.room.Embedded

data class Avatar(var id: String? = "",
                  @Embedded var variants: Variants?)