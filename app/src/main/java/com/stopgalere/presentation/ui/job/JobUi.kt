package com.stopgalere.presentation.ui.job

import androidx.compose.runtime.Immutable
import com.stopgalere.domain.model.Job
import kotlin.String

@Immutable
data class JobUi(
    val id: String,
    val title: String,
    val city: String,
    val date: String,
    val deadline: String,

    // details
    val description: String,
    val sectorName: String,
    val genderName: String,
    val contractTypeName: String,
    val workModeName: String,

    // author/company
    val authorEmail: String,
    val authorWebsite: String,
    val authorMobile1: String,
    val authorMobile2: String,
    val authorLongitude: Double?,
    val authorLatitude: Double?,
    val company: String,
    val companyLogoUrl: String,

    // misc
    val salary: String,
    val experience: String,
    val educationLevel: String
)

// Job UI dont have dateAdded
fun Job.toUi(): JobUi = JobUi(
    id = id,
    title = title,
    city = city.ifBlank { "—"} ,
    date = date.ifBlank { "—" },
    deadline = deadline,
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
