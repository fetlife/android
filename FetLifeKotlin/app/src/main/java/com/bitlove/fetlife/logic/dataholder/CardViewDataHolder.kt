package com.bitlove.fetlife.logic.dataholder

import android.arch.persistence.room.Ignore
import com.bitlove.fetlife.getBaseUrl
import com.bitlove.fetlife.hash

abstract class CardViewDataHolder {

    @Ignore
    var contentHash: String? = null

    //TODO: Clean up sync object hierarchy
    open fun getLocalId() : String? = null
    open fun getType() : String? = null
    open fun getRemoteId() : String? = null

    open fun getAvatar() : AvatarViewDataHolder? = null

    open fun getTitle() : String? = null
    open fun getSupportingText() : String? = null

    open fun getMediaUrl() : String? = null
    open fun getMediaAspectRatio() : Float? = null

    open fun getCommentCountText() : String? = null
    open fun getCommentCount() : Int {
        return try {
            getCommentCountText()?.toInt() ?: 0
        } catch (e: NumberFormatException) {
            0
        }
    }

    open fun hasNewComment() : Boolean? = false
    //open fun hasNewLove() : Boolean? = false
    open fun getLoveCount() : String? = null
    open fun isLoved() : Boolean? = null

    open fun getComments() : List<ReactionViewDataHolder>? = null
    open fun getLoves() : List<ReactionViewDataHolder>? = null

    open fun displayComments() : Boolean? = null
    open fun isDeletable() : Boolean? = false


    open fun isSame(other: CardViewDataHolder): Boolean {
        return this.getRemoteId() == other.getRemoteId() && this.getType() == this.getType()
    }

    open fun hasSameContent(other: CardViewDataHolder): Boolean {
        return genContentHash() == other.genContentHash()
    }

    private fun genContentHash() : String {
        if (contentHash == null) {
            contentHash = (
                    getAvatar()?.genContentHash() +
                    getTitle() +
                    getSupportingText() +
                    getMediaUrl()?.getBaseUrl() +
                    getMediaAspectRatio() +
                    getCommentCountText() +
                    hasNewComment() +
                    getLoveCount() +
                    isLoved() +
                    getComments()?.size +
                    getLoves()?.size
            ).hash()
        }
        return contentHash!!
    }

}