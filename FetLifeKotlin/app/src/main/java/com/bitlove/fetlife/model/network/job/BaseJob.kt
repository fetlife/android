package com.bitlove.fetlife.model.network.job

import android.arch.lifecycle.MediatorLiveData
import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.getLoggedInUserId
import com.bitlove.fetlife.model.dataobject.entity.technical.JobProgressEntity
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import com.bitlove.fetlife.model.db.FetLifeContentDatabaseWrapper
import com.bitlove.fetlife.model.network.FetLifeApi
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.*
import kotlin.reflect.full.primaryConstructor

abstract class BaseJob(jobPriority: Int, doPersist: Boolean, val userId: String?, vararg tags: String) : Job(createParams(jobPriority,doPersist,*tags)) {
    companion object {

        val PRIORITY_LOGIN = 0

        //priority change cancel reason range
        val PRIORITY_CHANGE_RANGE_LOW = 1111
        val PRIORITY_CHANGE_RANGE_HIGH = 1999

        //priorities
        val PRIORITY_MODIFY_RESOURCE = 1111
        val PRIORITY_GET_RESOURCE_FRONT = 1222
        val PRIORITY_GET_RESOURCE_BACKGROUND = 1333
        val PRIORITY_UPLOAD_MEDIA = 1555

        //tags
        val TAG_GET_RESOURCE = "TAG_GET_RESOURCE"
        val TAG_SYNC_RESOURCE = "TAG_SYNC_RESOURCE"
    }

    open var progressTrackerId: String = UUID.randomUUID().toString()
    val progressTrackerLiveData : MediatorLiveData<ProgressTracker> = MediatorLiveData()

    init {
        bg {
            val jobProgressEntity = JobProgressEntity(progressTrackerId, ProgressTracker.STATE.NEW.toString())
            val jobProgressDao = getDatabaseWrapper().lockDb(userId)?.jobProgressDao()
            jobProgressDao?.insert(jobProgressEntity)
            if (jobProgressDao != null) {
                progressTrackerLiveData.addSource(jobProgressDao.getTracker(progressTrackerId),{data -> progressTrackerLiveData.value = data})
            }
            getDatabaseWrapper().releaseDb()
        }
    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint {
        return RetryConstraint.CANCEL
    }

    abstract fun onRunJob() : Boolean

    final override fun onRun() {
        if (onRunJob()) {
            updateProgressState(ProgressTracker.STATE.FINISHED)
        } else {
            updateProgressState(ProgressTracker.STATE.FAILED,getJobMessage())
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

    private fun updateProgressState(state: ProgressTracker.STATE, message: String? = null) {
        bg {
            val jobProgressEntity = JobProgressEntity(progressTrackerId, state.toString(), message)
            val jobProgressDao = getDatabaseWrapper().lockDb(userId)?.jobProgressDao()
            jobProgressDao?.update(jobProgressEntity)
            getDatabaseWrapper().releaseDb()
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
