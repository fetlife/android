package com.bitlove.fetlife.model.resource.get

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.getLoggedInUser
import com.bitlove.fetlife.model.dataobject.entity.content.RelationEntity
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Group
import com.bitlove.fetlife.model.dataobject.wrapper.Relation
import com.bitlove.fetlife.model.db.FetLifeContentDatabase

class GetGroupResource(val groupId: String, forceLoad: Boolean, userId : String? = getLoggedInUser()?.getLocalId()) : GetResource<Relation>(forceLoad, userId) {

    override fun loadFromDb(contentDb: FetLifeContentDatabase): LiveData<Relation> {
        return contentDb.relationDao().getGroupRelation(groupId)
    }

    override fun shouldSync(data: Relation?, forceSync: Boolean): Boolean {
        //TODO : Consider using expiration time
        return forceSync
    }

    override fun syncWithNetwork(data: Relation?) {
        //TODO : card detail call; get type from db
    }
}