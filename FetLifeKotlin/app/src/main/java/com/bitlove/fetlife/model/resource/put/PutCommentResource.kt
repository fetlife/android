package com.bitlove.fetlife.model.resource.put

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.network.job.put.PutCommentJob

class PutCommentResource : PutResource<Reaction>() {

    private val reactionDao = FetLifeApplication.instance.fetLifeContentDatabase.reactionDao()

    override fun saveToDb(reaction: Reaction) {
        reactionDao.insert(reaction.reactionEntity)
    }

    override fun shouldSync(reaction: Reaction): Boolean {
        return false
    }

    override fun syncWithNetwork(reaction: Reaction) {
        FetLifeApplication.instance.jobManager.addJobInBackground(PutCommentJob(reaction))
    }
}