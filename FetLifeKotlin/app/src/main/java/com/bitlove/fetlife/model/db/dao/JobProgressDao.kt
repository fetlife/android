package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.bitlove.fetlife.model.dataobject.entity.technical.JobProgressEntity
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker

@Dao
abstract class JobProgressDao : BaseDao<JobProgressEntity> {

    @Query("SELECT * FROM job_progress WHERE id = :id")
    abstract fun getTracker(id: String): LiveData<ProgressTracker?>

    @Query("DELETE FROM job_progress")
    abstract fun deleteAll()

}