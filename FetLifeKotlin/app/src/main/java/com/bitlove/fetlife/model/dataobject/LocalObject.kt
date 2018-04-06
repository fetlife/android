package com.bitlove.fetlife.model.dataobject

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.DataEntity
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.db.dao.BaseDao

interface LocalObject<T : DataEntity> {
    open fun getLocalId() : String?

    fun getDao() : BaseDao<T>
    open fun getEntity() : T

    @Suppress("UNCHECKED_CAST")
    fun save() {
        getDao().update(this.getEntity())
    }

    @Suppress("UNCHECKED_CAST")
    fun delete() {
        getDao().delete(getEntity())
    }

    fun getDataBase() : FetLifeContentDatabase {
        return FetLifeApplication.instance.fetLifeContentDatabase
    }
}