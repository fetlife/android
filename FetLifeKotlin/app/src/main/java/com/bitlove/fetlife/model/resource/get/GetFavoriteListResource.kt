package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUserId
import com.bitlove.fetlife.model.dataobject.wrapper.Favorite
import com.bitlove.fetlife.model.db.FetLifeContentDatabase

class GetFavoriteListResource(forceLoad: Boolean, val page: Int, val limit: Int, userId : String? = getLoggedInUserId()) : GetResource<List<Favorite>>(forceLoad, userId) {

    override fun loadFromDb(contentDb: FetLifeContentDatabase): LiveData<List<Favorite>> {
        return contentDb.favoriteDao().getFavorites()
    }

    override fun shouldSync(data: List<Favorite>?, forceSync: Boolean): Boolean {
        return false
    }

    override fun syncWithNetwork(data: List<Favorite>?) {}

}