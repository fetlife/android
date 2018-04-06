package com.bitlove.fetlife.model.network.job.put

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.DataEntity
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.BaseJob
import retrofit2.Call

abstract class PutResourceJob<T : DataEntity>(open val dataObject: SyncObject<T>, jobPriority: Int, doPersist: Boolean, vararg tags: String) : BaseJob(jobPriority,doPersist,*tags) {

    override fun onRun() {
        val result = getCall().execute()
        if (result.isSuccessful) {
            try {
                saveToDb(result.body()!!)
            } catch (e: IllegalStateException) {
                //TODO: handle db closed
            }
        } else {
            //TODO notify
            //TODO fallback?
        }
    }

    abstract fun saveToDb(body: T)

    abstract fun getCall(): Call<T>

    open fun getDatabase() : FetLifeContentDatabase {
        return FetLifeApplication.instance.fetLifeContentDatabase
    }

}
