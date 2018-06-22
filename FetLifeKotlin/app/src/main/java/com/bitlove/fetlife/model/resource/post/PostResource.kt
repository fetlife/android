package com.bitlove.fetlife.model.resource.post

import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.resource.BaseResource
import com.bitlove.fetlife.model.resource.ResourceResult
import org.jetbrains.anko.coroutines.experimental.bg

abstract class PostResource<ResourceType>(val resource: ResourceType, userId: String?) : BaseResource<ResourceType>(userId) {

    override fun execute() : ResourceResult<ResourceType> {
        putInBackground(resource)
        return super.execute()
    }

    private fun putInBackground(resource: ResourceType) {
        getContentDatabaseWrapper().safeRun(userId, {
            contentDb ->
            saveToDb(contentDb, resource)
        },false)
        bg {
            if (shouldSync(resource)) {
                syncWithNetwork(resource)
            }
        }
    }

    abstract fun saveToDb(contentDb: FetLifeContentDatabase, resource: ResourceType)

    abstract fun shouldSync(data: ResourceType): Boolean

    abstract fun syncWithNetwork(data: ResourceType)

}