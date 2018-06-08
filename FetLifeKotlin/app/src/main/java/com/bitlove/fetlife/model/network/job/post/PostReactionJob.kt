package com.bitlove.fetlife.model.network.job.post

import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.content.ReactionEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import retrofit2.Call

//TODO: solve persistence
class PostReactionJob(var reaction: SyncObject<ReactionEntity>, var parent: SyncObject<ContentEntity>, userId: String? = getLoggedInUser()?.getLocalId()) : PostResourceJob<ReactionEntity>(reaction, PRIORITY_MODIFY_RESOURCE,false, userId, POST_REACTION, TAG_SYNC_RESOURCE) {

    companion object {
        const val POST_REACTION = "POST_REACTION"
    }

    override fun saveToDb(contentDb: FetLifeContentDatabase, entity: ReactionEntity) {
        val localEntity = reaction.getEntity()
        entity.type = localEntity.type
        entity.memberId = localEntity.memberId
        entity.contentId = localEntity.contentId
        reaction.delete()
        contentDb.reactionDao().insertOrUpdate(entity)
    }

    override fun getCall(): Call<*> {
        val parentEntity = parent.getEntity()
        return when (reaction.getEntity().type) {
            Reaction.TYPE.COMMENT.toString() -> {
                when (parentEntity.type) {
                    Content.TYPE.CONVERSATION.toString() -> getApi().postMessage(getAuthHeader(), parent.getRemoteId()!!, reaction.getEntity().body!!)
                    else -> getApi().postComment(getAuthHeader(), parentEntity.remoteMemberId, parent.getServerType(), parentEntity.networkId, reaction.getEntity().body!!)
                }
            }
            else -> {
                getApi().putLove(getAuthHeader(),parentEntity.networkId,parent.getServerType()!!)
            }
        }
    }

}