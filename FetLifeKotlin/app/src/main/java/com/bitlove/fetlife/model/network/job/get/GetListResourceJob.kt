package com.bitlove.fetlife.model.network.job.get

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.DataEntity
import com.bitlove.fetlife.model.db.FetLifeDatabase
import com.bitlove.fetlife.model.network.job.BaseJob
import retrofit2.Call

abstract class GetListResourceJob<T : DataEntity>(jobPriority: Int, doPersist: Boolean, vararg tags: String) : BaseJob(jobPriority,doPersist,*tags) {

    override fun onRun() {
        if (FetLifeApplication.instance.fetlifeService.authToken == null) {
            FetLifeApplication.instance.fetlifeService.login()
        }

        val result = getCall().execute()
        if (result.isSuccessful){
            saveToDb(result.body()!!)
        } else {
            //TODO notify
        }
    }

    open fun getDatabase() : FetLifeDatabase {
        return FetLifeApplication.instance.fetLifeDatabase
    }

    abstract fun saveToDb(resourceArray: Array<T>)

    abstract fun getCall(): Call<Array<T>>

}
