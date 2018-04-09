package com.bitlove.fetlife.logic.dataholder

abstract class AvatarViewDataHolder {

    open fun getLocalId() : String? = null
    open fun getAvatarUrl() : String? = null
    open fun getAvatarName() : String? = null
    open fun getAvatarMeta() : String? = ""
    open fun getAvatarSubline() : String? = null
    open fun getAvatarSublineExtra() : String? = ""

    open fun isSame(other: AvatarViewDataHolder): Boolean {
        return this.getLocalId() == other.getLocalId()
    }

    open fun hasSameContent(other: AvatarViewDataHolder): Boolean {
        if (this.getAvatarUrl() != other.getAvatarUrl()) return false
        if (this.getAvatarName() != other.getAvatarName()) return false
        if (this.getAvatarMeta() != other.getAvatarMeta()) return false
        if (this.getAvatarSubline() != other.getAvatarSubline()) return false
        if (this.getAvatarSublineExtra() != other.getAvatarSublineExtra()) return false
        return true
    }

}