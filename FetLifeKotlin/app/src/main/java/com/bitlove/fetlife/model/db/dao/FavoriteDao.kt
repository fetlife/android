package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.entity.content.FavoriteEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Favorite

@Dao
abstract class FavoriteDao : BaseDao<FavoriteEntity> {

    @Query("SELECT * FROM favorites")
    abstract fun getFavorites(): LiveData<List<Favorite>>

    @Query("SELECT * FROM favorites WHERE dbId = :dbId ORDER BY createdAt DESC")
    abstract fun getFavorite(dbId: String): LiveData<Favorite>

}