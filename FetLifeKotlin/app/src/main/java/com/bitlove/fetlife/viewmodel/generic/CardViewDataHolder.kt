package com.bitlove.fetlife.viewmodel.generic

abstract class CardViewDataHolder {

    open fun getLocalId() : String? = null
    open fun getAvatar() : AvatarViewDataHolder? = null
    open fun getMediaUrl() : String? = null
    open fun getTitle() : String? = null
    open fun getSupportingText() : String? = null

    open fun hasNewComment() : Boolean? = false
    open fun getComments() : List<ReactionViewDataHolder>? = null
    //open fun hasNewLove() : Boolean? = false
    open fun getLoves() : List<ReactionViewDataHolder>? = null

    open fun isSame(other: CardViewDataHolder): Boolean {
        return this.getLocalId() == other.getLocalId()
    }

    open fun hasSameContent(other: CardViewDataHolder): Boolean {
        if (this.getTitle() != other.getTitle()) return false
        val thisAvatar = this.getAvatar()
        val otherAvatar = other.getAvatar()
        when {
            thisAvatar == null -> if (otherAvatar != null) return false
            otherAvatar == null -> return false
            else -> if (!thisAvatar.hasSameContent(otherAvatar)) return false
        }
        if (this.getAvatar() != other.getAvatar()) return false
        if (this.getSupportingText() != other.getSupportingText()) return false
        if (this.getMediaUrl() != other.getMediaUrl()) return false
        val thisComments = this.getComments()
        val otherComments = other.getComments()
        when {
            thisComments == null -> if (otherComments != null) return false
            otherComments == null -> return false
            thisComments.size != otherComments.size -> return false
            else -> for ((i,comment) in thisComments.withIndex()) {
                if (!comment.hasSameContent(otherComments[i])) {
                    return false
                }
            }
        }
        val thisLoves = this.getLoves()
        val otherLoves = other.getLoves()
        when {
            thisLoves == null -> if (otherLoves != null) return false
            otherLoves == null -> return false
            thisLoves.size != otherLoves.size -> return false
            else -> for ((i,love) in thisLoves.withIndex()) {
                if (!love.hasSameContent(otherLoves[i])) {
                    return false
                }
            }
        }
        return true
    }

}