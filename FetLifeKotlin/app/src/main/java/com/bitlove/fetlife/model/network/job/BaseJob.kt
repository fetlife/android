package com.bitlove.fetlife.model.network.job

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import com.birbit.android.jobqueue.TagConstraint
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.technical.JobProgressEntity
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.db.FetLifeContentDatabaseWrapper
import com.bitlove.fetlife.model.network.FetLifeApi
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.*
import kotlin.reflect.full.primaryConstructor

abstract class BaseJob(jobPriority: Int, doPersist: Boolean, val userId: String?, vararg tags: String, val uid: UUID = UUID.randomUUID()) : Job(createParams(jobPriority,doPersist,*tags,uid.toString())) {
    companion object {

        const val PRIORITY_LOGIN = 0

        //priority change cancel reason range
        const val PRIORITY_CHANGE_RANGE_LOW = 1111
        const val PRIORITY_CHANGE_RANGE_HIGH = 1999

        //priorities
        const val PRIORITY_DELETE_RESOURCE = 11
        const val PRIORITY_MODIFY_RESOURCE = 1111
        const val PRIORITY_GET_RESOURCE_FRONT = 1222
        const val PRIORITY_GET_RESOURCE_BACKGROUND = 1333
        const val PRIORITY_UPLOAD_MEDIA = 1555

        //tags
        const val TAG_GET_RESOURCE = "TAG_GET_RESOURCE"
        const val TAG_SYNC_RESOURCE = "TAG_SYNC_RESOURCE"
        const val TAG_DELETE_RESOURCE = "TAG_DELETE_RESOURCE"

        const val FINISHED_DELAY = 200L
    }

    open var progressTrackerId: String = UUID.randomUUID().toString()
    val progressTrackerLiveData : MediatorLiveData<ProgressTracker> = MediatorLiveData()
    open var lastTrackerSource : LiveData<ProgressTracker?>? = null

    init {
        updateProgressState(ProgressTracker.STATE.NEW, 0L, null,true)
    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint {
        return RetryConstraint.CANCEL
    }

    abstract fun onRunJob() : Boolean

    final override fun onRun() {
        updateProgressState(ProgressTracker.STATE.IN_PROGRESS)
        if (onRunJob()) {
            updateProgressState(ProgressTracker.STATE.FINISHED,FINISHED_DELAY)
        } else {
            updateProgressState(ProgressTracker.STATE.FAILED,FINISHED_DELAY,getJobMessage())
        }
    }

    open fun getJobMessage(): String? {
        return null
    }

    open fun onAddedJob() {}

    final override fun onAdded() {
        updateProgressState(ProgressTracker.STATE.QUEUED)
        onAddedJob()
    }

    open fun onCancelJob(cancelReason: Int, throwable: Throwable?) {}

    final override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        if (PRIORITY_CHANGE_RANGE_LOW <= cancelReason && cancelReason <+ PRIORITY_CHANGE_RANGE_HIGH) {
            FetLifeApplication.instance.jobManager.addJobInBackground(cloneWithPriority(cancelReason))
        } else {
            updateProgressState(ProgressTracker.STATE.CANCELLED)
            onCancelJob(cancelReason,throwable)
        }
    }

    open fun updateProgressState(state: ProgressTracker.STATE, delay: Long = 0, message: String? = null, addSource: Boolean = false) {
        bg {
            //TODO: verify the need of this delay, verify the need of Anko (default kotlin my be more enhanced)
            Thread.sleep(delay)
            val jobProgressEntity = JobProgressEntity(progressTrackerId, state.toString(), message)
            getDatabaseWrapper().safeRun(userId, {
                contentDb ->
                val jobProgressDao = contentDb.jobProgressDao()
                if (addSource && jobProgressDao != null) {
                    if (lastTrackerSource != null) progressTrackerLiveData.removeSource(lastTrackerSource!!)
                    lastTrackerSource = jobProgressDao.getTracker(progressTrackerId)
                    progressTrackerLiveData.addSource(lastTrackerSource!!,{data -> progressTrackerLiveData.value = data})
                }
                jobProgressDao?.insertOrUpdate(jobProgressEntity)
            })
        }
    }

    open fun getDatabaseWrapper() : FetLifeContentDatabaseWrapper {
        return FetLifeApplication.instance.fetLifeContentDatabaseWrapper
    }

    open fun getApi() : FetLifeApi {
        return FetLifeApplication.instance.fetlifeService.fetLifeApi
    }

    open fun getAuthHeader() : String {
        return FetLifeApplication.instance.fetlifeService.authHeader!!
    }

    open fun cloneWithPriority(newPriority: Int): Job {
        return this::class.primaryConstructor!!.call(newPriority,isPersistent,tags, progressTrackerId)
    }
}

fun createParams(priority: Int, doPersist: Boolean, vararg tags: String): Params {
    val params = Params(priority)
    params.addTags(*tags).setPersistent(doPersist).setRequiresNetwork(true)
    return params
}
