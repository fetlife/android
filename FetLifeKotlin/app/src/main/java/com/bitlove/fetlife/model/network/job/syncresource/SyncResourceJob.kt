package com.bitlove.fetlife.model.network.job.syncresource

import com.bitlove.fetlife.model.dataobject.DataObject
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.model.network.job.BaseJob
import retrofit2.Call

abstract class SyncResourceJob<T : DataObject>(open val dataObject: T, jobPriority: Int, doPersist: Boolean, vararg tags: String) : BaseJob(jobPriority,doPersist,*tags) {

    override fun onRun() {
        getDao().insert(dataObject)
        val result = getCall().execute()
        if (result.isSuccessful) {
            getDao().insert(result.body()!!)
        } else {
            //TODO notify
            //TODO fallback?
        }
    }

    abstract fun getDao() : BaseDao<T>

    abstract fun getCall(): Call<T>

}
