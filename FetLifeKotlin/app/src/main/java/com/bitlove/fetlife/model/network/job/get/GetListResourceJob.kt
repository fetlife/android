package com.bitlove.fetlife.model.network.job.get

import com.bitlove.fetlife.model.dataobject.entity.content.DataEntity
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.BaseJob
import retrofit2.Call
import retrofit2.Response

abstract class GetListResourceJob<T : DataEntity>(jobPriority: Int, doPersist: Boolean, userId: String?, vararg tags: String) : BaseJob(jobPriority,doPersist,userId,*tags) {

    override fun onRunJob() : Boolean {
        //Workaround for different feed result
        val result = getCall().execute()
        return if (result.isSuccessful){
            val contentDb = getDatabaseWrapper().lockDb(userId)
            contentDb?.runInTransaction { saveToDb(contentDb, getResultBody(result)) }
            getDatabaseWrapper().releaseDb()
            true
        } else {
            false
        }
    }

    open fun getResultBody(result: Response<*>) : Array<T> {
        return result.body() as Array<T>
    }

    //TODO(cleanup): cleanup save to dbs
    abstract fun saveToDb(contentDb: FetLifeContentDatabase, resourceArray: Array<T>)

    //Workaround * to support Feed vs Story Array
    abstract fun getCall(): Call<*>

}
