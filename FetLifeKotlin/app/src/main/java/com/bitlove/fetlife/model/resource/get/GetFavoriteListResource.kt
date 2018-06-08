package com.bitlove.fetlife.model.resource.get

import android.arch.paging.DataSource
import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.model.dataobject.wrapper.Favorite
import com.bitlove.fetlife.model.db.FetLifeContentDatabase

class GetFavoriteListResource(val limit: Int, userId : String? = getLoggedInUser()?.getLocalId()) : GetListResource<Favorite>(userId) {

    override fun loadListFromDb(contentDb: FetLifeContentDatabase): DataSource.Factory<Int, Favorite> {
        return contentDb.favoriteDao().getFavorites()
    }

    override fun syncWithNetwork(page: Int?, item: Favorite?) {}

}