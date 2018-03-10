package com.bitlove.fetlife.viewmodel.generic

abstract class CommentViewDataHolder {

    companion object {
        const val COMMENT_MAX_COUNT_EXPANDED = 3
        const val COMMENT_MAX_COUNT_COLLAPSED = 1
    }

    open fun getAppId() : String? = null
    open fun getAvatarUrl() : String? = null
    open fun getAvatarName() : String? = null
    open fun getText() : String? = null

    open fun isSame(other: CommentViewDataHolder): Boolean {
        return this.getAppId() == other.getAppId()
    }

    open fun hasSameContent(other: CommentViewDataHolder): Boolean {
        if (this.getAvatarName() != other.getAvatarName()) return false
        if (this.getAvatarUrl() != other.getAvatarUrl()) return false
        if (this.getText() != other.getText()) return false
        return true
    }
}