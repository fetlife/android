package com.bitlove.fetlife.logic.dataholder

import android.arch.persistence.room.Ignore
import com.bitlove.fetlife.getBaseUrl
import com.bitlove.fetlife.hash

abstract class AvatarViewDataHolder {

    @Ignore
    private var contentHash : String? = null

    open fun getLocalId() : String? = null
    open fun getRemoteId() : String? = null
    open fun getType() : String? = null

    open fun getAvatarUrl() : String? = null
    open fun getAvatarName() : String? = null
    open fun getAvatarMeta() : String? = ""
    open fun getAvatarSubline() : String? = null
    open fun getAvatarSublineExtra() : String? = ""

    open fun isSame(other: AvatarViewDataHolder): Boolean {
        return this.getRemoteId() == other.getRemoteId() && this.getType() == this.getType()
    }

    open fun hasSameContent(other: AvatarViewDataHolder): Boolean {
        return genContentHash() == other.genContentHash()
    }

    fun genContentHash() : String {
        if (contentHash == null) {
            contentHash = (getAvatarName() + getAvatarUrl()?.getBaseUrl() + getAvatarMeta() + getAvatarSubline() + getAvatarSublineExtra()).hash()
        }
        return contentHash!!
    }

}