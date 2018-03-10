package com.bitlove.fetlife.model.dataobject.temp

import android.arch.persistence.room.Embedded
import com.google.gson.annotations.SerializedName

data class ExploreEvent(@Embedded(prefix = "event_member_")  var member: RefMember,
                        var action: String = "",
                        @Embedded var target: Target)