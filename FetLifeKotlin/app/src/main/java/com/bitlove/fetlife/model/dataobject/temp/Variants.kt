package com.bitlove.fetlife.model.dataobject.temp

import android.arch.persistence.room.Embedded

data class Variants(@Embedded(prefix = "small_") var small: Small?,
                    @Embedded(prefix = "mini_") var mini: Mini?,
                    @Embedded(prefix = "large_") var large: Large?,
                    @Embedded(prefix = "huge_") var huge: Huge?,
                    @Embedded(prefix = "medium_") var medium: Medium?,
                    @Embedded(prefix = "full_") var full: Full?)