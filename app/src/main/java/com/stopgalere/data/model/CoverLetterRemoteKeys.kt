package com.stopgalere.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cover_letter_remote_keys")
data class CoverLetterRemoteKeys(
    @PrimaryKey val coverLetterId: String,
    val prevKey: Int?,
    val nextKey: Int?
)
