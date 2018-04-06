package com.bitlove.fetlife.model.dataobject.entity.reference

import com.bitlove.fetlife.model.dataobject.entity.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.embedded.Avatar
import com.google.gson.annotations.SerializedName

data class MemberRef(@SerializedName("id") var id: String = "",
                     @SerializedName("nickname") var nickname: String?  = "",
                     @SerializedName("meta_line") var metaInfo: String?,
                     @SerializedName("avatar") var avatar: Avatar?) {

    fun asEntity(): MemberEntity {
        val entity = MemberEntity()
        entity.networkId = id
        entity.nickname = nickname
        entity.metaInfo = metaInfo
        entity.avatar = avatar
        return entity
    }

    fun asEntity(mergeEntity: MemberEntity): MemberEntity {
        if (nickname != null) {
            mergeEntity.nickname = nickname
        }
        if (avatar != null) {
            mergeEntity.avatar = avatar
        }
        if (metaInfo != null) {
            mergeEntity.metaInfo = metaInfo
        }
        return mergeEntity
    }
}