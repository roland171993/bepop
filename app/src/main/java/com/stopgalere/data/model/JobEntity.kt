package com.stopgalere.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stopgalere.domain.model.Job

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val id: String,

    // display
    val title: String,
    val city: String?,

    // legacy (your UI may bind to this)
    val date: String?,

    // details
    val description: String?,

    // nested refs -> store names only (IDs skipped)
    val sectorName: String?,
    val genderName: String?,
    val contractTypeName: String?,
    val workModeName: String?,

    // author/company
    val authorEmail: String?,
    val authorWebsite: String?,
    val authorMobile1: String?,
    val authorLongitude: String?,
    val authorLatitude: String?,
    val company: String?,
    val companyLogoUrl: String?,

    // misc
    val salary: String?,
    val experience: String?,
    val educationLevel: String?,

    // timestamps kept
    val dateAdded: String?,
    val updatedAt: String?
)

fun JobEntity.toDomain() = Job(
    id = id,
    title = title,
    city = city,
    date = date,

    description = description,
    sectorName = sectorName,
    genderName = genderName,
    contractTypeName = contractTypeName,
    workModeName = workModeName,

    authorEmail = authorEmail,
    authorWebsite = authorWebsite,
    authorMobile1 = authorMobile1,
    authorLongitude = authorLongitude,
    authorLatitude = authorLatitude,
    company = company,
    companyLogoUrl = companyLogoUrl,

    salary = salary,
    experience = experience,
    educationLevel = educationLevel,

    dateAdded = dateAdded,
    updatedAt = updatedAt
)
