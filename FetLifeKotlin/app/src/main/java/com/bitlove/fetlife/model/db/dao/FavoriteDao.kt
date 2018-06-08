package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.entity.content.FavoriteEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Favorite

@Dao
abstract class FavoriteDao : BaseDao<FavoriteEntity> {

    @Query("SELECT * FROM favoriteSingleItem")
    abstract fun getFavorites(): DataSource.Factory<Int,Favorite>

    @Query("SELECT * FROM favoriteSingleItem WHERE dbId = :dbId ORDER BY createdAt DESC")
    abstract fun getFavorite(dbId: String): LiveData<Favorite>

}