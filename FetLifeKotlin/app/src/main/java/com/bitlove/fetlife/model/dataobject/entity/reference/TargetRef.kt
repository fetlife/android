package com.bitlove.fetlife.model.dataobject.entity.reference

import android.text.TextUtils
import com.google.gson.annotations.SerializedName

data class TargetRef(
        @SerializedName("comment") var comment: ReactionRef? = null,
        @SerializedName("love") var love: ReactionRef? = null,
        @SerializedName("video") var video: Video? = null,
        @SerializedName("picture") var picture: Picture? = null,
        @SerializedName("writing") var writing: Writing? = null,
        @SerializedName("peopleInto") var peopleInto: RelationRef? = null,
        @SerializedName("relation") var relation: RelationRef? = null,
        @SerializedName("group_membership") var groupMembership: RelationRef? = null,
        @SerializedName("group_post") var groupPost: Writing? = null,
        @SerializedName("rsvp") var rsvp: RelationRef? = null,
        @SerializedName("status") var status: ReactionRef? = null,
        @SerializedName("wall_post") var wallPost: Writing? = null) {

     var id : String? = ""
        get() {
            if (TextUtils.isEmpty(field)) {
                field = generateId()
            }
            return field
        }

    var createdAt: String? = null
        get() {
            return when {
                comment != null -> comment!!.createdAt
                love != null -> love!!.createdAt
                video != null -> video!!.createdAt
                picture != null -> picture!!.createdAt
                writing != null -> writing!!.createdAt
                peopleInto != null -> peopleInto!!.createdAt
                relation != null -> relation!!.createdAt
                groupMembership != null -> groupMembership!!.createdAt
                groupPost != null -> groupPost!!.createdAt
                rsvp != null -> rsvp!!.createdAt
                status != null -> status!!.createdAt
                wallPost != null -> wallPost!!.createdAt
                else -> null
            }
        }

    private fun generateId(): String {
        var dbId = ""
        dbId += picture?.id?:""
        dbId += writing?.id?:""
        dbId += love?.id?:""
        return dbId
    }

}

