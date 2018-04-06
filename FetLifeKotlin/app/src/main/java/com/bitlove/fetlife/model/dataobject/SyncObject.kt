package com.bitlove.fetlife.model.dataobject

import com.bitlove.fetlife.model.dataobject.entity.DataEntity

interface SyncObject<T : DataEntity> : LocalObject<T> {
    open fun getRemoteId() : String?
}