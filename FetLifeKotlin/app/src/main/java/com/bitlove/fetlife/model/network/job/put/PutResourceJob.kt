package com.bitlove.fetlife.model.network.job.put

import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.DataEntity
import com.bitlove.fetlife.model.network.job.BaseJob
import retrofit2.Call

abstract class PutResourceJob<T : DataEntity>(open val dataObject: SyncObject<T>, jobPriority: Int, doPersist: Boolean, vararg tags: String) : BaseJob(jobPriority,doPersist,*tags) {

    override fun onRunJob(): Boolean {
        val result = getCall().execute()
        return if (result.isSuccessful) {
            try {
                saveToDb(result.body()!!)
            } catch (e: IllegalStateException) {
                //TODO: handle db closed
            }
            true
        } else {
            //TODO fallback?
            false
        }
    }

    abstract fun saveToDb(body: T)

    abstract fun getCall(): Call<T>

}
