package com.bitlove.fetlife.model.db

import android.arch.persistence.room.Room
import android.arch.persistence.room.Transaction
import com.bitlove.fetlife.FetLifeApplication
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

//TODO: consider implementing multi user support
class FetLifeContentDatabaseWrapper {

    companion object {
        const val INIT_WAIT_TIME_SECONDS = 2L
        const val EXECUTE_WAIT_TIME_SECONDS = 15L
        const val RELEASE_WAIT_TIME_SECONDS = 120L
    }

    private lateinit var userId: String
    private var keepOpen: Boolean = true

    private val lock = ReentrantLock()
    private var contentDb : FetLifeContentDatabase? = null

    fun init(userId : String, keepOpen : Boolean = true) {
        if (lock.tryLock(INIT_WAIT_TIME_SECONDS,TimeUnit.SECONDS)) {
            try {
                this.userId = userId
                this.keepOpen = keepOpen
                openDb()
            } finally {
                lock.unlock()
            }
        } else {
            throw IllegalStateException()
        }
    }

    fun release(userId: String) {
        if (userId != this.userId) return
        if (lock.tryLock(RELEASE_WAIT_TIME_SECONDS, TimeUnit.SECONDS)) {
            try {
                this.keepOpen = false
                closeDb()
            } finally {
                lock.unlock()
            }
        } else {
            throw IllegalStateException()
        }
    }

    fun safeRun(userId: String?, runner: (FetLifeContentDatabase) -> Unit, runInTransaction: Boolean = false) : Boolean {
        if (userId == null) throw IllegalArgumentException()
        if (userId != this.userId) return false
        if (!lock.tryLock(EXECUTE_WAIT_TIME_SECONDS, TimeUnit.SECONDS)) return false
        try {
            if (contentDb == null) {
                openDb()
            }
            if (runInTransaction) {
                contentDb!!.runInTransaction({runner.invoke(contentDb!!)})
            } else {
                runner.invoke(contentDb!!)
            }
            return true
        } catch (t: Throwable) {
            //TODO: log
            return false
        } finally {
            if (!keepOpen) {
                closeDb()
            }
            lock.unlock()
        }
    }

    private fun closeDb() {
        contentDb?.close()
        contentDb = null
    }

    private fun openDb() {
        contentDb = Room.databaseBuilder(FetLifeApplication.instance, FetLifeContentDatabase::class.java, "fetlife_database_" + userId).fallbackToDestructiveMigration().build()
    }

}