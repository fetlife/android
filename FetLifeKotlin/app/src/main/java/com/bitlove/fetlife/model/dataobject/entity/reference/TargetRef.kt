package com.bitlove.fetlife.model.dataobject.entity.reference

import android.text.TextUtils

data class TargetRef(
        var love: ReactionRef? = null,
        var picture: Picture? = null,
        var writing: Writing? = null) {

     var id : String? = ""
        get() {
            if (TextUtils.isEmpty(field)) {
                field = generateId()
            }
            return field
        }

    var createdAt: String? = null
        get() {
            return when {
                love != null -> love!!.createdAt
                picture != null -> picture!!.createdAt
                writing != null -> writing!!.createdAt
                else -> null
            }
        }

    private fun generateId(): String {
        var dbId = ""
        dbId += picture?.id?:""
        dbId += writing?.id?:""
        dbId += love?.id?:""
        return dbId
    }

}

