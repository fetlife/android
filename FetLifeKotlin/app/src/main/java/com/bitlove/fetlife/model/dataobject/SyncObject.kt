package com.bitlove.fetlife.model.dataobject

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUserId
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
    fun save(userId: String? = getLoggedInUserId()) {
        val contentDb = getDataBaseWrapper().lockDb(userId)
        if (contentDb != null) {
            val dao = getDao(contentDb)
            dao.insertOrUpdate(this.getEntity())
            getDataBaseWrapper().releaseDb()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun delete(userId: String? = getLoggedInUserId()) {
        val contentDb = getDataBaseWrapper().lockDb(userId)
        if (contentDb != null) {
            val dao = getDao(contentDb)
            dao.delete(getEntity())
            getDataBaseWrapper().releaseDb()
        }
    }

    fun getDataBaseWrapper() : FetLifeContentDatabaseWrapper {
        return FetLifeApplication.instance.fetLifeContentDatabaseWrapper
    }
}