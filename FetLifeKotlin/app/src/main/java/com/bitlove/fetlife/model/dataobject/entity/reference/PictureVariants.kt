package com.bitlove.fetlife.model.dataobject.entity.reference

import android.arch.persistence.room.Embedded
import com.google.gson.annotations.SerializedName

data class PictureVariants(@Embedded(prefix = "small") @SerializedName("small") var small: PictureVariant?,
                           @Embedded(prefix = "mini") @SerializedName("mini") var mini: PictureVariant?,
                           @Embedded(prefix = "large") @SerializedName("large") var large: PictureVariant?,
                           @Embedded(prefix = "huge") @SerializedName("huge") var huge: PictureVariant?,
                           @Embedded(prefix = "medium") @SerializedName("medium") var medium: PictureVariant?,
                           @Embedded(prefix = "full") @SerializedName("full") var full: PictureVariant?)