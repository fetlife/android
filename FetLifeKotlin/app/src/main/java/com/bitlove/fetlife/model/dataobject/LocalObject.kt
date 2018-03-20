package com.bitlove.fetlife.model.dataobject

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.DataEntity
import com.bitlove.fetlife.model.db.FetLifeDatabase
import com.bitlove.fetlife.model.db.dao.BaseDao

interface LocalObject<in T : DataEntity> {
    open fun getLocalId() : String?

    fun getDao() : BaseDao<T>

    @Suppress("UNCHECKED_CAST")
    fun save() {
        getDao().update(this as T)
    }

    @Suppress("UNCHECKED_CAST")
    fun delete() {
        getDao().delete(this as T)
    }

    fun getDataBase() : FetLifeDatabase {
        return FetLifeApplication.instance.fetLifeDatabase
    }
}