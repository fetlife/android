package com.bitlove.fetlife.model.resource.post

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.FavoriteEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ReactionEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Favoritable
import com.bitlove.fetlife.model.dataobject.wrapper.Member
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.post.PostReactionJob

class SetFavoriteResource(favoritable: Favoritable, userId: String? = getLoggedInUser()?.getLocalId()) : PostResource<Favoritable>(favoritable, userId) {

    override fun saveToDb(contentDb: FetLifeContentDatabase, favoritable: Favoritable) {
        val data = favoritable?.getChild()?: favoritable
        val currentFavoriteEntity = favoritable.getFavoriteEntity()
        if (currentFavoriteEntity != null) {
            contentDb.favoriteDao().delete(currentFavoriteEntity)
        } else {
            val favoriteEntity = FavoriteEntity()
            when(data) {
                is Content -> {
                    favoriteEntity.contentId = data.getLocalId()
                }
                is Member -> {
                    favoriteEntity.memberId = data.getLocalId()
                }
                else -> return
            }
            contentDb.favoriteDao().insertOrUpdate(favoriteEntity)
        }
    }

    override fun shouldSync(favoritable: Favoritable): Boolean {
        return false
    }

    override fun syncWithNetwork(favoritable: Favoritable) {
    }

}