package com.stopgalere.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_remote_keys")
data class JobRemoteKeys(
    @PrimaryKey val jobId: String,
    val prevKey: Int?,
    val nextKey: Int?
)
