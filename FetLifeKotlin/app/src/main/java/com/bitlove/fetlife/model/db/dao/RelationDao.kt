package com.bitlove.fetlife.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import android.util.Log
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import com.bitlove.fetlife.model.dataobject.entity.RelationEntity
import com.bitlove.fetlife.model.dataobject.entity.reference.ReactionRef
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction

@Dao
abstract class RelationDao : BaseDao<RelationEntity> {

}