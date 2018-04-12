package com.bitlove.fetlife.model.dataobject.entity.technical

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.bitlove.fetlife.model.dataobject.entity.content.DataEntity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "job_progress")
class JobProgressEntity(@PrimaryKey @SerializedName("id") var id: String,
                        @SerializedName("state") var state: String?  = null,
                        @SerializedName("message") var message: String?  = null) : DataEntity