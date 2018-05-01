package com.bitlove.fetlife.model.dataobject.wrapper

import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.entity.content.FavoriteEntity

interface Favoritable {
    fun getLocalId(): String?
    fun getFavoriteEntity(): FavoriteEntity?
    fun getChild(): CardViewDataHolder?
}