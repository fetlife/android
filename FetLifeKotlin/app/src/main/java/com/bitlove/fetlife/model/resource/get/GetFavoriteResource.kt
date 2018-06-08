package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.model.dataobject.wrapper.Favorite
import com.bitlove.fetlife.model.db.FetLifeContentDatabase

class GetFavoriteResource(private val favId: String, forceLoad: Boolean, userId: String? = getLoggedInUser()?.getLocalId()) : GetResource<Favorite>(forceLoad, userId) {

    override fun loadFromDb(contentDb: FetLifeContentDatabase): LiveData<Favorite> {
        return contentDb.favoriteDao().getFavorite(favId)
    }

    override fun shouldSync(data: Favorite?, forceSync: Boolean): Boolean {
        return false
    }

    override fun syncWithNetwork(data: Favorite?) {
        //TODO : card detail call; get type from db
    }
}