package com.bitlove.fetlife.model.db.dao

import android.arch.persistence.room.Dao
import com.bitlove.fetlife.model.dataobject.entity.content.RelationEntity

@Dao
abstract class RelationDao : BaseDao<RelationEntity>