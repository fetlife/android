package com.bitlove.fetlife.model.resource

import android.arch.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import com.birbit.android.jobqueue.TagConstraint
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUserId
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker
import com.bitlove.fetlife.model.db.FetLifeContentDatabaseWrapper
import com.bitlove.fetlife.model.network.job.BaseJob
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

abstract class BaseResource<ResourceType>(val userId : String?) {

    //TODO: consider more jobs in case of paging?
    private val jobIds = ArrayList<String>()

    open var loadResult = object : ResourceResult<ResourceType>(){
        override fun execute(): ResourceResult<ResourceType> = this@BaseResource.execute()
        override fun cancel() = this@BaseResource.cancel()
        override fun loadMore() = this@BaseResource.loadMore()
        //TODO: implement change priority
        override fun reducedPriority(): Boolean = this@BaseResource.reducePriority()
        override fun normalPriority(): Boolean = this@BaseResource.increasePriority()
    }

    fun addJob(job: BaseJob, unique: Boolean = true) {
        bg {
            synchronized(jobIds,{
                getJobManager().cancelJobs(TagConstraint.ALL,*jobIds.toArray(Array(jobIds.size,{""})))
                jobIds.clear()
                setProgressTracker(job!!.progressTrackerLiveData)
                jobIds.add(job.uid.toString())
            })
            getJobManager().addJobInBackground(job)
        }
    }

    open fun execute() : ResourceResult<ResourceType> = loadResult
    open fun loadMore(): ResourceResult<ResourceType> = loadResult

    //TODO: move into BaseJob code / cancel + priorities
    open fun cancel() {
        bg {
            synchronized(jobIds,{
                getJobManager().cancelJobs(TagConstraint.ALL,*jobIds.toArray(Array(jobIds.size,{""})))
                jobIds.clear()
            })
        }
    }

    open fun reducePriority() = false
    open fun increasePriority() = false

    open fun setProgressTracker(progressTracker: LiveData<ProgressTracker>) {
        loadResult.progressTracker.addSource(progressTracker, {data -> loadResult.progressTracker.value = data})
    }

    fun getContentDatabaseWrapper() : FetLifeContentDatabaseWrapper {
        return FetLifeApplication.instance.fetLifeContentDatabaseWrapper
    }

    fun getJobManager() : JobManager {
        return FetLifeApplication.instance.jobManager
    }

}