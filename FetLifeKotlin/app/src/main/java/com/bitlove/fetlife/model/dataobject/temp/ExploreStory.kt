package com.bitlove.fetlife.model.dataobject.temp

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.bitlove.fetlife.model.dataobject.DataObject
import com.bitlove.fetlife.viewmodel.generic.CardViewDataHolder

@Entity
data class ExploreStory(var name: String = "",
                        @Embedded var event: ExploreEvent) : DataObject, CardViewDataHolder() {

    @PrimaryKey var storyAppId : String = ""
        get() {return event.target.picture.id}

    override fun getAppId(): String {
        return  storyAppId
    }

    override fun getServerId(): String {
        return ""
    }

    override fun getAvatarName(): String? {
        return event.member.nickname
    }

    override fun getAvatarMeta(): String? {
        return event.member.metaLine
    }

    override fun getAvatarUrl(): String? {
        return event.member.url
    }

    override fun getMediaUrl(): String? {
        return event.target.picture.variants.huge?.url
    }

    override fun getSupportingText(): String? {
        return event.target.picture.body
    }
}