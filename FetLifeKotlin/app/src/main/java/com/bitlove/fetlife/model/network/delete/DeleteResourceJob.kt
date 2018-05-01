package com.bitlove.fetlife.model.network.delete

import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.content.DataEntity
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.network.job.BaseJob
import retrofit2.Call

abstract class DeleteResourceJob<T : DataEntity>(open val dataObject: SyncObject<T>, jobPriority: Int, doPersist: Boolean, userId: String?, vararg tags: String) : BaseJob(jobPriority,doPersist,userId,*tags) {

    override fun onRunJob(): Boolean {
        val result = getCall().execute()
        return if (result.isSuccessful) {
            getDatabaseWrapper().safeRun(userId, {
                contentDb ->
                deleteFromDb(contentDb)
            },true)
            true
        } else {
            //TODO fallback?
            false
        }
    }

    abstract fun deleteFromDb(contentDb: FetLifeContentDatabase)

    abstract fun getCall(): Call<*>

}
