package com.bitlove.fetlife.model.network.job.get

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.DataEntity
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.BaseJob
import retrofit2.Call

abstract class GetListResourceJob<T : DataEntity>(jobPriority: Int, doPersist: Boolean, vararg tags: String) : BaseJob(jobPriority,doPersist,*tags) {

    override fun onRun() {
        val result = getCall().execute()
        if (result.isSuccessful){
            try {
                saveToDb(result.body()!!)
            } catch (e: IllegalStateException) {
                //TODO: handle db closed
            }
        } else {
            //TODO notify
        }
    }

    open fun getDatabase() : FetLifeContentDatabase {
        return FetLifeApplication.instance.fetLifeContentDatabase
    }

    abstract fun saveToDb(resourceArray: Array<T>)

    abstract fun getCall(): Call<Array<T>>

}
