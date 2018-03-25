package com.bitlove.fetlife.model.resource.put

import org.jetbrains.anko.coroutines.experimental.bg

abstract class PutResource<in ResourceType> {

    fun put(resource: ResourceType) {
        putInBackground(resource)
    }

    private fun putInBackground(resource: ResourceType) {
        bg {
            saveToDb(resource)
            if (shouldSync(resource)) {
                syncWithNetwork(resource)
            }
        }
    }

    abstract fun saveToDb(resource: ResourceType)

    abstract fun shouldSync(data: ResourceType): Boolean

    abstract fun syncWithNetwork(data: ResourceType)

}