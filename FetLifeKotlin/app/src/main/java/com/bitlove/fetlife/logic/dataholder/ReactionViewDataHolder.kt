package com.bitlove.fetlife.logic.dataholder

import android.arch.persistence.room.Ignore
import com.bitlove.fetlife.hash

abstract class ReactionViewDataHolder : CardViewDataHolder() {

    @Ignore
    private var reactionHash : String? = null

    open fun getText() : String? = null
    open fun getTime() : String? = null

    override fun getContentHash() : String {
        if (reactionHash == null) {
            reactionHash = super.getContentHash() + (getAvatar()?.getContentHash() + getText()).hash()
        }
        return reactionHash!!
    }
}