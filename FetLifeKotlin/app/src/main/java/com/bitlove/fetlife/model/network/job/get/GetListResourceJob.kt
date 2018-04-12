package com.bitlove.fetlife.model.network.job.get

import com.bitlove.fetlife.model.dataobject.entity.content.DataEntity
import com.bitlove.fetlife.model.network.job.BaseJob
import retrofit2.Call
import retrofit2.Response

abstract class GetListResourceJob<T : DataEntity>(jobPriority: Int, doPersist: Boolean, vararg tags: String) : BaseJob(jobPriority,doPersist,*tags) {

    override fun onRunJob() : Boolean {
        //Workaround for different feed result
        val result = getCall().execute()
        return if (result.isSuccessful){
            try {
                getDatabase().runInTransaction { saveToDb(getResultBody(result)) }
            } catch (e: IllegalStateException) {
                //TODO(cleanup): solve closed db
            }
            true
        } else {
            false
        }
    }

    open fun getResultBody(result: Response<*>) : Array<T> {
        return result.body() as Array<T>
    }

    //TODO(cleanup): cleanup save to dbs
    abstract fun saveToDb(resourceArray: Array<T>)

    //Workaround * to support Feed vs Story Array
    abstract fun getCall(): Call<*>

}
