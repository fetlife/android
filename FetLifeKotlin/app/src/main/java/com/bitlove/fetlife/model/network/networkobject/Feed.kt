package com.bitlove.fetlife.model.network.networkobject

import com.bitlove.fetlife.model.dataobject.entity.content.ExploreStoryEntity
import com.google.gson.annotations.SerializedName

data class Feed(@SerializedName("stories") var stories: Array<ExploreStoryEntity>?)
