package com.bitlove.fetlife.datasource.network.job

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import com.bitlove.fetlife.FetLifeApplication
import kotlin.reflect.full.primaryConstructor

abstract class BaseJob(jobPriority: Int, doPersist: Boolean, vararg tags: String) : Job(createParams(jobPriority,doPersist,*tags)) {
    companion object {
        val PRIORITY_UI_FRONT = 111
        val PRIORITY_UI_BACKGROUND = 222
        val PRIORITY_UPLOAD = 555

        val PRIORITY_CHANGE_UI_BACKGROUND = 111

        val TAG_UI = "TAG_UI"
    }

    final override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        if (cancelReason == PRIORITY_CHANGE_UI_BACKGROUND) {
            FetLifeApplication.instance.jobManager.addJobInBackground(cloneWithPriority(PRIORITY_UI_BACKGROUND))
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
