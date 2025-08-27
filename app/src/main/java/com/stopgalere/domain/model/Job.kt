package com.stopgalere.domain.model

data class Job(
    val id: String,
    val title: String,
    val city: String?,

    // dates
    val date: String?,          // normalized for UI (dd-MM-yyyy)
    val dateAdded: String?,     // raw from API (ISO, etc.) for Room sorting

    // details
    val description: String?,
    val sectorName: String?,
    val genderName: String?,
    val contractTypeName: String?,
    val workModeName: String?,

    // author/company
    val authorEmail: String?,
    val authorWebsite: String?,
    val authorMobile1: String?,
    val authorLongitude: Double?,
    val authorLatitude: Double?,
    val company: String?,
    val companyLogoUrl: String?,

    // misc
    val salary: Int?,
    val experience: String?,
    val educationLevel: String?
)

