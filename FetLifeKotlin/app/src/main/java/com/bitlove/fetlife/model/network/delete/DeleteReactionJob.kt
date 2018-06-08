package com.bitlove.fetlife.model.network.delete

import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ReactionEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import retrofit2.Call

//TODO: solve persistence
class DeleteReactionJob(var reaction: SyncObject<ReactionEntity>, var parent: SyncObject<ContentEntity>, userId: String? = getLoggedInUser()?.getLocalId()) : DeleteResourceJob<ReactionEntity>(reaction, PRIORITY_DELETE_RESOURCE,false, userId, DELETE_REACTION, TAG_DELETE_RESOURCE) {

    companion object {
        const val DELETE_REACTION = "DELETE_REACTION"
    }

    override fun deleteFromDb(contentDb: FetLifeContentDatabase) {
        //TODO: consider removing from stuff you love
        contentDb.reactionDao().delete(reaction.getEntity())
    }

    override fun getCall(): Call<*> {
        val parentEntity = parent.getEntity()
        return when (reaction.getEntity().type) {
            Reaction.TYPE.LOVE.toString() -> {
                return getApi().deleteLove(getAuthHeader(),parentEntity.networkId,parent.getServerType()!!)
            }
            else -> throw (IllegalArgumentException())
        }
    }

}