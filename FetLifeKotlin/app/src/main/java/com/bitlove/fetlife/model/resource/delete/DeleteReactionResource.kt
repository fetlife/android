package com.bitlove.fetlife.model.resource.delete

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.model.dataobject.entity.content.ReactionEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.delete.DeleteReactionJob
import com.bitlove.fetlife.model.network.job.post.PostReactionJob

class DeleteReactionResource(reaction: Reaction, val parent: Content, userId: String? = getLoggedInUser()?.getLocalId()) : DeleteResource<Reaction>(reaction, userId) {

    companion object {
        fun newDeleteLoveResource(parent: Content): DeleteReactionResource {
            val reactionEntity = ReactionEntity()
            reactionEntity.memberId = FetLifeApplication.instance.loggedInUser!!.getLocalId()
            reactionEntity.type = Reaction.TYPE.LOVE.toString()
            val reaction = Reaction()
            reaction.reactionEntity = reactionEntity
            return DeleteReactionResource(reaction, parent)
        }
    }

    override fun removeFromDb(contentDb: FetLifeContentDatabase, reaction: Reaction) {
        if (reaction.getType() == Reaction.TYPE.LOVE.toString()) {
            parent.contentEntity.loved = false
            parent.save(userId)
        }
        val entity = reaction.getEntity()
        entity.contentId = parent.getLocalId()
        reaction.delete(userId)
    }

    override fun shouldSync(reaction: Reaction): Boolean {
        return true
    }

    override fun syncWithNetwork(reaction: Reaction) {
        addJob(DeleteReactionJob(reaction, parent))
    }

}