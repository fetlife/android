package com.bitlove.fetlife.logic.dataholder

import android.arch.persistence.room.Ignore
import com.bitlove.fetlife.hash

abstract class ReactionViewDataHolder {

    @Ignore
    private var contentHash : String? = null

    open fun getLocalId() : String? = null
    open fun getRemoteId() : String? = null
    open fun getType() : String? = null
    open fun getAvatar() : AvatarViewDataHolder? = null
    open fun getText() : String? = null

    open fun isSame(other: ReactionViewDataHolder): Boolean {
        return this.getRemoteId() == other.getRemoteId() && this.getType() == this.getType()
    }

    open fun hasSameContent(other: ReactionViewDataHolder): Boolean {
        return genContentHash() == other.genContentHash()
    }

    protected fun genContentHash() : String {
        if (contentHash == null) {
            contentHash = (getAvatar()?.genContentHash() + getText()).hash()
        }
        return contentHash!!
    }
}