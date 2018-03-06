package com.bitlove.fetlife.model.network.job

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import com.bitlove.fetlife.FetLifeApplication
import kotlin.reflect.full.primaryConstructor

abstract class BaseJob(jobPriority: Int, doPersist: Boolean, vararg tags: String) : Job(createParams(jobPriority,doPersist,*tags)) {
    companion object {
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

    final override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        if (PRIORITY_CHANGE_RANGE_LOW <= cancelReason && cancelReason <+ PRIORITY_CHANGE_RANGE_HIGH) {
            FetLifeApplication.instance.jobManager.addJobInBackground(cloneWithPriority(cancelReason))
        } else {
            onCancelJob(cancelReason,throwable)
        }
    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint {
        return RetryConstraint.CANCEL
    }

    override fun onAdded() {
    }

    open fun onCancelJob(cancelReason: Int, throwable: Throwable?) {
    }

    open fun cloneWithPriority(newPriority: Int): Job {
        return this::class.primaryConstructor!!.call(newPriority,isPersistent,tags)
    }
}

fun createParams(priority: Int, doPersist: Boolean, vararg tags: String): Params {
    val params = Params(priority)
    params.addTags(*tags).setPersistent(doPersist).setRequiresNetwork(true)
    return params
}
