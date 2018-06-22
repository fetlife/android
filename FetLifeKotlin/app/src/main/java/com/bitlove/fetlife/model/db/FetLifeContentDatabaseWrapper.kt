package com.bitlove.fetlife.model.db

import android.arch.persistence.room.Room
import android.arch.persistence.room.Transaction
import android.util.Log
import com.bitlove.fetlife.FetLifeApplication
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock

//TODO: consider implementing multi user support
class FetLifeContentDatabaseWrapper {

    companion object {
        const val INIT_WAIT_TIME_SECONDS = 2L
        const val EXECUTE_WAIT_TIME_SECONDS = 15L
        const val RELEASE_WAIT_TIME_SECONDS = 120L
    }

    private lateinit var userId: String
    private var keepOpen: Boolean = true

    private val lock = ReentrantReadWriteLock()
    private var contentDb : FetLifeContentDatabase? = null

    fun init(userId : String, keepOpen : Boolean = true) {
        if (lock.writeLock().tryLock(INIT_WAIT_TIME_SECONDS,TimeUnit.SECONDS)) {
            try {
                this.userId = userId
                this.keepOpen = keepOpen
                openDb()
            } finally {
                lock.writeLock().unlock()
            }
        } else {
            throw IllegalStateException()
        }
    }

    fun safeRelease(userId: String) {
        if (userId != this.userId) return
        if (lock.writeLock().tryLock(RELEASE_WAIT_TIME_SECONDS, TimeUnit.SECONDS)) {
            try {
                this.keepOpen = false
                closeDb()
            } finally {
                lock.writeLock().unlock()
            }
        } else {
            throw IllegalStateException()
        }
    }

    fun safeRun(userId: String?, runner: (FetLifeContentDatabase) -> Unit, runInTransaction: Boolean = false) : Boolean {
        if (userId == null) throw IllegalArgumentException()
        if (userId != this.userId) return false
        val t = System.currentTimeMillis()
        val ui = UUID.randomUUID().toString()
        Log.e("DBDBD",ui + " starts waiting for lock")
        if (!lock.readLock().tryLock(EXECUTE_WAIT_TIME_SECONDS, TimeUnit.SECONDS)) return false
        Log.e("DBDBD",ui + " got through in " + (System.currentTimeMillis() - t).toString())
        try {
            if (contentDb == null) {
                openDb()
            }
//TODO: check if there is an option to avoid blocking transactiions
//            if (runInTransaction) {
//                contentDb!!.runInTransaction({runner.invoke(contentDb!!)})
//            } else {
                runner.invoke(contentDb!!)
//            }
            return true
        } catch (t: Throwable) {
            //TODO: log
            return false
        } finally {
            lock.readLock().unlock()
            if (!keepOpen && lock.writeLock().tryLock()) {
                try {
                    closeDb()
                } finally {
                    lock.writeLock().unlock()
                }
            }
        }
    }

    private fun closeDb() {
        contentDb?.close()
        contentDb = null
    }

    private fun openDb() {
        contentDb = Room.databaseBuilder(FetLifeApplication.instance, FetLifeContentDatabase::class.java, "fetlife_database_" + userId).fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }

}