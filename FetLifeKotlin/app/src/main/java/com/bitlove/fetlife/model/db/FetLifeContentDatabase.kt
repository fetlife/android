package com.bitlove.fetlife.model.db

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.bitlove.fetlife.model.dataobject.entity.content.*
import com.bitlove.fetlife.model.dataobject.entity.technical.JobProgressEntity
import com.bitlove.fetlife.model.db.dao.*

@Database(entities = arrayOf(ContentEntity::class, EventEntity::class, ExploreEventEntity::class, ExploreStoryEntity::class, GroupEntity::class, MemberEntity::class, ReactionEntity::class, RelationEntity::class, JobProgressEntity::class, FavoriteEntity::class), version = 10)
//TODO: db result merging with server order using in dbid
abstract class FetLifeContentDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun memberDao(): MemberDao
    abstract fun reactionDao(): ReactionDao
    abstract fun relationDao(): RelationDao
    abstract fun exploreStoryDao(): ExploreStoryDao
    abstract fun exploreEventDao(): ExploreEventDao
    abstract fun jobProgressDao(): JobProgressDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun groupDao(): GroupDao
}
