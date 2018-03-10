package com.bitlove.fetlife.model.dataobject

import com.bitlove.fetlife.model.db.dao.BaseDao

interface DataObject {

    open fun getAppId() : String
    open fun getServerId() : String

}