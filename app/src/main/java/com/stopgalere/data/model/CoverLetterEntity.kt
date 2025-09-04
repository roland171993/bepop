package com.stopgalere.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stopgalere.domain.model.CoverLetter

/*
  Keep the same "date" (UI) + "dateAdded" (raw)
  dateAdded uses createdAt (raw ISO) for sort stability in SQL.
 */
@Entity(tableName = "cover_letters")
data class CoverLetterEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,

    // timeline
    val date: String,          // UI-friendly (dd-MM-yyyy) from createdAt
    val dateAdded: String,     // raw createdAt, used for ORDER BY
)

fun CoverLetterEntity.toDomain() = CoverLetter(
    id = id,
    title = title,
    content = content,
    date = date,
    dateAdded = dateAdded
)
