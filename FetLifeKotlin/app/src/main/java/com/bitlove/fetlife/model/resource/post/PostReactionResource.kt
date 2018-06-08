package com.bitlove.fetlife.model.resource.post

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.model.dataobject.entity.content.ReactionEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.post.PostReactionJob
import com.bitlove.fetlife.toServerTime

class PostReactionResource(reaction: Reaction, val parent: Content, userId: String? = getLoggedInUser()?.getLocalId()) : PostResource<Reaction>(reaction, userId) {

    companion object {
        fun newPostCommentResource(body: String, parent: Content) : PostReactionResource {
            val reactionEntity = ReactionEntity()
            reactionEntity.body = body
            //TODO(cleanup) add createdAt
            //TODO(cleanup) check logged in state
            reactionEntity.memberId = FetLifeApplication.instance.loggedInUser!!.getLocalId()
            reactionEntity.type = Reaction.TYPE.COMMENT.toString()
            reactionEntity.createdAt = System.currentTimeMillis().toServerTime()
            val reaction = Reaction()
            reaction.reactionEntity = reactionEntity
            return PostReactionResource(reaction, parent)
        }
        fun newPostLoveResource(parent: Content): PostReactionResource {
            val reactionEntity = ReactionEntity()
            reactionEntity.memberId = FetLifeApplication.instance.loggedInUser!!.getLocalId()
            reactionEntity.type = Reaction.TYPE.LOVE.toString()
            val reaction = Reaction()
            reaction.reactionEntity = reactionEntity
            return PostReactionResource(reaction, parent)
        }
    }

    override fun saveToDb(contentDb: FetLifeContentDatabase, reaction: Reaction) {
        if (reaction.getType() == Reaction.TYPE.LOVE.toString()) {
            parent.contentEntity.loved = true
            parent.save(userId)
        }
        val entity = reaction.getEntity()
        entity.contentId = parent.getLocalId()
        reaction.save(userId)
        parent.save(userId)
    }

    override fun shouldSync(reaction: Reaction): Boolean {
        return true
    }

    override fun syncWithNetwork(reaction: Reaction) {
        addJob(PostReactionJob(reaction, parent))
    }

}