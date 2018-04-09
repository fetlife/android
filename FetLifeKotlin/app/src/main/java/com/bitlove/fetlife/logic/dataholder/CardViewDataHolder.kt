package com.bitlove.fetlife.logic.dataholder

import android.arch.persistence.room.Ignore
import com.bitlove.fetlife.hash

abstract class CardViewDataHolder {

    @Ignore
    var hash: String? = null

    //TODO: Clean up sync object hierarchy
    open fun getLocalId() : String? = null
    open fun getRemoteId() : String? = null
    open fun getAvatar() : AvatarViewDataHolder? = null
    open fun getMediaUrl() : String? = null
    open fun getBaseMediaUrl(): String? {
        val mediaUrl = getMediaUrl()
        return mediaUrl?.substring(0,mediaUrl.indexOf('?',0))
    }
    open fun getMediaAspectRatio() : Float? = null
    open fun getTitle() : String? = null
    open fun getSupportingText() : String? = null
    open fun getCommentCount() : String? = null
    open fun getLoveCount() : String? = null
    open fun isLoved() : Boolean? = null

    open fun displayComments() : Boolean? = null
    open fun isDeletable() : Boolean? = false
    open fun hasNewComment() : Boolean? = false
    open fun getComments() : List<ReactionViewDataHolder>? = null
    //open fun hasNewLove() : Boolean? = false
    open fun getLoves() : List<ReactionViewDataHolder>? = null

    open fun isSame(other: CardViewDataHolder): Boolean {
        return this.getRemoteId() == other.getRemoteId()
    }

    open fun hasSameContent(other: CardViewDataHolder): Boolean {
        return hash() == other.hash()
    }

    private fun hash() : String {
        //TODO: implement properly
        if (hash == null) {
            hash = (getBaseMediaUrl() + getSupportingText() + getTitle() + getComments()?.size).hash()
        }
        return hash!!
    }

}