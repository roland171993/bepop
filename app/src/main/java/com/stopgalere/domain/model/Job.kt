package com.stopgalere.domain.model

data class Job(
    val id: String,
    val title: String,
    val city: String?,

    // legacy UI field you already use
    val date: String?,

    // details
    val description: String?,
    val sectorName: String?,        // no sectorId
    val genderName: String?,        // no genderId
    val contractTypeName: String?,  // no contractTypeId
    val workModeName: String?,      // no workModeId

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

    // timestamps we keep
    val dateAdded: String?,
    val updatedAt: String?
)
