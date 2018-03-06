package com.bitlove.fetlife.viewmodel.generic

abstract class CommentViewDataHolder {

    companion object {
        const val COMMENT_MAX_COUNT_EXPANDED = 3
        const val COMMENT_MAX_COUNT_COLLAPSED = 1
    }

    open fun getDataId() : String? = null
    open fun getAvatarUrl() : String? = null
    open fun getAvatarName() : String? = null
    open fun getText() : String? = null

    open fun isSame(other: CardViewDataHolder): Boolean {
        return this.getDataId() == other.getDataId()
    }

    open fun hasSameContent(other: CommentViewDataHolder): Boolean {
        //TODO test this
        //should be OK for data classes as long no array is used
        return equals(other)
//        if (this.getAvatarName() != other.getAvatarName()) return false
//        if (this.getAvatarUrl() != other.getAvatarUrl()) return false
//        if (this.getText() != other.getText()) return false
//        return true
    }
}