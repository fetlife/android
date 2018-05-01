package com.bitlove.fetlife.model.network.job.post

import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.DataEntity
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.BaseJob
import retrofit2.Call

abstract class PostResourceJob<T : DataEntity>(open val dataObject: SyncObject<T>, jobPriority: Int, doPersist: Boolean, userId: String?, vararg tags: String) : BaseJob(jobPriority,doPersist,userId,*tags) {

    override fun onRunJob(): Boolean {
        val result = getCall().execute()
        return if (result.isSuccessful) {
            val resultBody = result.body() as? T
            if (resultBody != null) {
                getDatabaseWrapper().safeRun(userId, {
                    contentDb ->
                    saveToDb(contentDb,resultBody)
                },true)
            }
            true
        } else {
            //TODO fallback?
            false
        }
    }

    abstract fun saveToDb(contentDb: FetLifeContentDatabase, body: T)

    abstract fun getCall(): Call<*>

}
