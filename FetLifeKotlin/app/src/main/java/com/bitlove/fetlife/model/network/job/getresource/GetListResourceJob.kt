package com.bitlove.fetlife.model.network.job.getresource

import com.bitlove.fetlife.model.dataobject.DataObject
import com.bitlove.fetlife.model.db.dao.BaseDao
import com.bitlove.fetlife.model.network.job.BaseJob
import retrofit2.Call

abstract class GetListResourceJob<T : DataObject>(jobPriority: Int, doPersist: Boolean, vararg tags: String) : BaseJob(jobPriority,doPersist,*tags) {

    override fun onRun() {
        val result = getCall().execute()
        if (result.isSuccessful){
            saveToDb(result.body()!!)
        } else {
            //TODO notify
        }
    }

    open fun saveToDb(resourceArray: Array<T>) {
        getDao().insert(*resourceArray)
    }

    abstract fun getDao() : BaseDao<T>

    abstract fun getCall(): Call<Array<T>>

}
