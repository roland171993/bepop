package com.stopgalere.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stopgalere.domain.model.Job

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val id: String,
    val title: String,
    val city: String?,
    val date: String?
)

fun JobEntity.toDomain() = Job(id, title, city, date)
fun Job.toEntity() = JobEntity(id, title, city, date)
