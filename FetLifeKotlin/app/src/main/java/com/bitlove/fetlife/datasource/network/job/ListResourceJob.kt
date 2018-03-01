package com.bitlove.fetlife.datasource.network.job

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import com.bitlove.fetlife.datasource.dataobject.CardData
import com.bitlove.fetlife.datasource.db.dao.BaseDao
import retrofit2.Call

abstract class ListResourceJob<T : CardData>(jobPriority: Int, doPersist: Boolean, vararg tags: String) : BaseJob(jobPriority,doPersist,*tags) {

    override fun onRun() {
        val result = getCall().execute()
        if (result.isSuccessful){
            getDao().insert(*result.body()!!)
        } else {
            //TODO notify
        }
    }

    abstract fun getDao() : BaseDao<T>

    abstract fun getCall(): Call<Array<T>>

}
