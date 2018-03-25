package com.bitlove.fetlife.model.dataobject.entity.reference

import android.text.TextUtils

data class TargetRef(var picture: Picture) {

     var id : String? = ""
        get() {
            if (TextUtils.isEmpty(field)) {
                field = generateId()
            }
            return field
        }

    private fun generateId(): String {
        var dbId = ""
        dbId += picture?.id
        return dbId
    }

}

