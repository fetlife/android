package com.bitlove.fetlife.logic.dataholder

abstract class ReactionViewDataHolder {

    companion object {
        const val COMMENT_MAX_COUNT_EXPANDED = 3
        const val COMMENT_MAX_COUNT_COLLAPSED = 1
    }

    open fun getLocalId() : String? = null
    open fun getAvatar() : AvatarViewDataHolder? = null
    open fun getText() : String? = null

    open fun isSame(other: ReactionViewDataHolder): Boolean {
        return this.getLocalId() == other.getLocalId()
    }

    open fun hasSameContent(other: ReactionViewDataHolder): Boolean {
        val thisAvatar = this.getAvatar()
        val otherAvatar = other.getAvatar()
        when {
            thisAvatar == null -> if (otherAvatar != null) return false
            otherAvatar == null -> return false
            else -> if (!thisAvatar.hasSameContent(otherAvatar)) return false
        }
        if (this.getAvatar() != other.getAvatar()) return false
        if (this.getText() != other.getText()) return false
        return true
    }
}