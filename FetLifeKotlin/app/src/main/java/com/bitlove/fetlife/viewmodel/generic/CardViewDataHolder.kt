package com.bitlove.fetlife.viewmodel.generic

import android.arch.persistence.room.Ignore
import android.databinding.ObservableField
import android.view.View
import com.bitlove.fetlife.model.dataobject.Comment

abstract class CardViewDataHolder {

    @Ignore
    open var commentsDisplayed = ObservableField<Boolean>(false)

    abstract fun getDataId() : String
    open fun getAvatarUrl() : String? = null
    open fun getAvatarName() : String? = null
    open fun getAvatarMeta() : String? = ""
    open fun getAvatarSubline() : String? = null
    open fun getAvatarSublineExtra() : String? = ""
    open fun getMediaUrl() : String? = null
    open fun getTitle() : String? = null
    open fun getSupportingText() : String? = null
    open fun getComments() : List<Comment>? = null

    open fun isSame(other: CardViewDataHolder): Boolean {
        return this.getDataId() == other.getDataId()
    }

    open fun hasSameContent(other: CardViewDataHolder): Boolean {
        //TODO test this
        //should be OK for data classes as long no array is used
        return equals(other)
//        if (this.getTitle() != other.getTitle()) return false
//        if (this.getAvatarUrl() != other.getAvatarUrl()) return false
//        if (this.getAvatarName() != other.getAvatarName()) return false
//        if (this.getAvatarMeta() != other.getAvatarMeta()) return false
//        if (this.getAvatarSubline() != other.getAvatarSubline()) return false
//        if (this.getAvatarSublineExtra() != other.getAvatarSublineExtra()) return false
//        if (this.getSupportingText() != other.getSupportingText()) return false
//        if (this.getMediaUrl() != other.getMediaUrl()) return false
//        val thisComments = this.getComments()
//        val otherComments = other.getComments()
//        when {
//            thisComments == null -> return otherComments == null
//            otherComments == null -> return false
//            thisComments.size != otherComments.size -> return false
//            else -> for ((i,comment) in thisComments.withIndex()) {
//                if (!comment.hasSameContent(otherComments[i])) {
//                    return false
//                }
//            }
//        }
//        return true
    }

}