package com.bitlove.fetlife.model.db

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.bitlove.fetlife.model.dataobject.entity.*
import com.bitlove.fetlife.model.db.dao.*

@Database(entities = arrayOf(ContentEntity::class, EventEntity::class, ExploreEventEntity::class, ExploreStoryEntity::class, GroupEntity::class, MemberEntity::class, ReactionEntity::class, RelationEntity::class), version = 1)
abstract class FetLifeDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun memberDao(): MemberDao
    abstract fun reactionDao(): ReactionDao
}
