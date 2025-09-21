package com.stopgalere.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stopgalere.domain.model.Job

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val id: String,

    // display
    val title: String,
    val city: String,

    // dates
    val date: String,          // normalized for UI (dd-MM-yyyy)
    val dateAdded: String,     // RAW from API for sorting in Room
    val deadline: String,

    // details
    val description: String,

    // nested refs -> store names only (IDs skipped)
    val sectorName: String,
    val genderName: String,
    val contractTypeName: String,
    val workModeName: String,

    // author/company
    val authorEmail: String,
    val authorWebsite: String,

    // default must be SQL literal: "''" (empty string)
    @ColumnInfo(defaultValue = "''")
    val authorMobile1: String,

    // new NOT NULL column with proper default
    @ColumnInfo(defaultValue = "''")
    val authorMobile2: String,

    val authorLongitude: Double?,
    val authorLatitude: Double?,
    val company: String,
    val companyLogoUrl: String,

    // misc
    val salary: String,
    val experience: String,
    val educationLevel: String,
)

fun JobEntity.toDomain() = Job(
    id = id,
    title = title,
    city = city,
    date = date,                 // UI uses normalized date
    deadline = deadline,
    dateAdded = dateAdded,
    description = description,
    sectorName = sectorName,
    genderName = genderName,
    contractTypeName = contractTypeName,
    workModeName = workModeName,
    authorEmail = authorEmail,
    authorWebsite = authorWebsite,
    authorMobile1 = authorMobile1,
    authorMobile2 = authorMobile2,
    authorLongitude = authorLongitude,
    authorLatitude = authorLatitude,
    company = company,
    companyLogoUrl = companyLogoUrl,
    salary = salary,
    experience = experience,
    educationLevel = educationLevel
)
