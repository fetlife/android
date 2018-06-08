package com.bitlove.fetlife.model.dataobject

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.model.dataobject.entity.content.DataEntity
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.db.FetLifeContentDatabaseWrapper
import com.bitlove.fetlife.model.db.dao.BaseDao

interface SyncObject<T : DataEntity> {

    fun getLocalId() : String?
    fun getRemoteId() : String?
    fun getServerType() : String? = null

    fun getDao(contentDatabase: FetLifeContentDatabase) : BaseDao<T>
    open fun getEntity() : T

    @Suppress("UNCHECKED_CAST")
    fun save(userId: String? = getLoggedInUser()?.getLocalId()) {
        getDataBaseWrapper().safeRun(userId, {
            contentDb ->
            val dao = getDao(contentDb)
            dao.insertOrUpdate(this.getEntity())
        })
    }

    @Suppress("UNCHECKED_CAST")
    fun delete(userId: String? = getLoggedInUser()?.getLocalId()) {
        getDataBaseWrapper().safeRun(userId, {
            contentDb ->
            val dao = getDao(contentDb)
            dao.delete(getEntity())
        })
    }

    fun getDataBaseWrapper() : FetLifeContentDatabaseWrapper {
        return FetLifeApplication.instance.fetLifeContentDatabaseWrapper
    }
}