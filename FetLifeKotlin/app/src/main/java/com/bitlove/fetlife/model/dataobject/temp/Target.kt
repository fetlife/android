package com.bitlove.fetlife.model.dataobject.temp

import android.arch.persistence.room.Embedded

data class Target(@Embedded var picture: Picture)