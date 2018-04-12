package com.bitlove.fetlife.model.dataobject

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.content.DataEntity
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.db.dao.BaseDao

interface SyncObject<T : DataEntity> {

    fun getLocalId() : String?
    fun getRemoteId() : String?

    fun getDao() : BaseDao<T>
    open fun getEntity() : T

    @Suppress("UNCHECKED_CAST")
    fun save() {
        val resultCount = getDao().update(this.getEntity())
        if (resultCount == 0) {
            getDao().insert(this.getEntity())
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun delete() {
        getDao().delete(getEntity())
    }

    fun getDataBase() : FetLifeContentDatabase {
        //TODO(db) : verify this
        return FetLifeApplication.instance.fetLifeContentDatabase
    }
}