package com.bitlove.fetlife.model.resource.delete

import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.resource.BaseResource
import com.bitlove.fetlife.model.resource.ResourceResult
import org.jetbrains.anko.coroutines.experimental.bg

abstract class DeleteResource<ResourceType>(val resource: ResourceType, userId: String?) : BaseResource<ResourceType>(userId) {

    override fun execute() : ResourceResult<ResourceType> {
        removeInBackground(resource)
        return super.execute()
    }

    private fun removeInBackground(resource: ResourceType) {
        getContentDatabaseWrapper().safeRun(userId, {
            contentDb ->
            removeFromDb(contentDb, resource)
        },false)
        bg {
            if (shouldSync(resource)) {
                syncWithNetwork(resource)
            }
        }
    }

    abstract fun removeFromDb(contentDb: FetLifeContentDatabase, resource: ResourceType)

    abstract fun shouldSync(data: ResourceType): Boolean

    abstract fun syncWithNetwork(data: ResourceType)

}