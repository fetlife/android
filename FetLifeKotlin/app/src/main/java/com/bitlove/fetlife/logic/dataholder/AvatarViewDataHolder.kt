package com.bitlove.fetlife.logic.dataholder

import android.arch.persistence.room.Ignore
import com.bitlove.fetlife.getBaseUrl
import com.bitlove.fetlife.hash

abstract class AvatarViewDataHolder : CardViewDataHolder() {

    @Ignore
    private var avatarHash : String? = null

    open fun getAvatarUrl() : String? = null
    open fun getAvatarName() : String? = null
    open fun getAvatarMeta() : String? = ""

    override fun getContentHash() : String {
        if (avatarHash == null) {
            avatarHash = super.getContentHash() + (getAvatarName() + getAvatarUrl()?.getBaseUrl() + getAvatarMeta()).hash()
        }
        return avatarHash!!
    }

}