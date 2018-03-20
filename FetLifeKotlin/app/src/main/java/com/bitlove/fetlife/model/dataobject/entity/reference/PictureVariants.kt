package com.bitlove.fetlife.model.dataobject.entity.reference

import com.google.gson.annotations.SerializedName

data class PictureVariants(@SerializedName("small") var small: PictureVariant?,
                           @SerializedName("mini") var mini: PictureVariant?,
                           @SerializedName("large") var large: PictureVariant?,
                           @SerializedName("huge") var huge: PictureVariant?,
                           @SerializedName("medium") var medium: PictureVariant?,
                           @SerializedName("full") var full: PictureVariant?)