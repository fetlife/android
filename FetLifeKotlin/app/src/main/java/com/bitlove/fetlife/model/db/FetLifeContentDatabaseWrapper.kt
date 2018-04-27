package com.bitlove.fetlife.model.db

import android.arch.persistence.room.Room
import com.bitlove.fetlife.FetLifeApplication
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

//TODO: consider implementing multi user support
//TODO: cleanup. user intransaction like runnable to enforce release
class FetLifeContentDatabaseWrapper {

    private lateinit var userId: String
    private var keepOpen: Boolean = true

    private val lock = ReentrantLock()
    private var contentDb : FetLifeContentDatabase? = null

    fun init(userId : String, keepOpen : Boolean = true) {
        lock.tryLock()
        this.userId = userId
        this.keepOpen = keepOpen
        open(keepOpen)
        lock.unlock()
    }

    fun tryLock() : Boolean{
        return lock.tryLock(10,TimeUnit.SECONDS)
    }

    fun tryOpen(userId: String?, keepOpen: Boolean = false) : Boolean {
        if (!lock.isHeldByCurrentThread) {
            throw IllegalAccessError("Use Lock before access")
        }
        if (userId == null || userId != this.userId) return false
        if (contentDb == null) {
            open(keepOpen)
        }
        return contentDb != null
    }

    fun lockDb(userId: String?) : FetLifeContentDatabase? {
        if (!tryLock() || !tryOpen(userId)) {
            releaseDb()
            return null
        }
        return contentDb!!
    }

    private fun open(keepOpen : Boolean) {
        this.keepOpen = keepOpen
        contentDb = Room.databaseBuilder(FetLifeApplication.instance, FetLifeContentDatabase::class.java, "fetlife_database_" + userId).build()
    }

    fun close(userId: String) {
        if (!lock.isHeldByCurrentThread) {
            throw IllegalAccessError("Use Lock before access")
        }
        contentDb?.close()
        contentDb = null
    }

    fun releaseDb() {
        if (!lock.isHeldByCurrentThread) {
            return
        }
        if (!keepOpen && lock.holdCount == 1 && contentDb?.isOpen == true) {
            contentDb?.close()
            contentDb = null
        }
        lock.unlock()
    }

}