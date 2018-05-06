package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import com.bitlove.fetlife.model.dataobject.entity.technical.JobProgressEntity

class ProgressTracker {

    enum class STATE {
        NEW,
        INITIATED,
        QUEUED,
        IN_PROGRESS,
        FAILED,
        CANCELLED,
        FINISHED
    }

    @Embedded lateinit var jobProgressEntity: JobProgressEntity

    fun inProgress(): Boolean {
        return getState() == STATE.IN_PROGRESS || getState() == STATE.QUEUED
    }

    fun getState(): STATE {
        return if (jobProgressEntity.state == null) STATE.NEW else STATE.valueOf(jobProgressEntity!!.state!!)
    }

    fun isFailed(): Boolean {
        return getState() == STATE.FAILED
    }

    fun isFinished(): Boolean {
        return getState() == STATE.FINISHED
    }

    fun isExecuted() : Boolean {
        return isFinished() || isFailed()
    }

    fun getMessage(): String? {
        return jobProgressEntity.message
    }

}