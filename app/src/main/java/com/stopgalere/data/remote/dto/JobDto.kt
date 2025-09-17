package com.stopgalere.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.stopgalere.data.model.JobEntity
import com.stopgalere.domain.validation.JobValidation

data class NamedRefDto(
    val id: String? = null,   // parsed but not stored (IDs are skipped)
    val name: String? = null
)

data class JobDto(
    @SerializedName("_id") val id: String? = null,
    val title: String? = null,
    val description: String? = null,

    // nested (we'll keep only the *names* downstream)
    val sector: NamedRefDto? = null,
    val gender: NamedRefDto? = null,
    val contractType: NamedRefDto? = null,
    val workMode: NamedRefDto? = null,

    // author/company
    val authorEmail: String? = null,
    val authorWebsite: String? = null,
    val authorMobile1: String? = null,
    val authorMobile2: String? = null,
    val authorLongitude: Double? = null,
    val authorLatitude: Double? = null,
    val company: String? = null,
    val companyLogoUrl: String? = null,

    // misc
    val salary: Int? = null,
    val city: String? = null,
    val experience: String? = null,
    val educationLevel: String? = null,

    // timestamps we keep
    @SerializedName("dateAdded") val dateAdded: String? = null,
    val deadline: String? = null
)

data class JobPagination(
    val total: Int? = null,
    val limit: Int? = null,
    val currentPage: Int? = null,
    val lastPage: Int? = null,
    val previousPage: Int? = null,
    val nextPage: Int? = null
)

data class JobsResponse(
    val jobs: List<JobDto> = emptyList(),
    val pagination: JobPagination? = null
)

data class JobDetailResponse(
    val job: JobDto
)

fun JobDto.toEntity(): JobEntity? {
    val validItem = JobValidation.validate(this) ?: return null
    return JobEntity(
        id = validItem.id,
        title = validItem.title,
        city = validItem.city,
        date = validItem.date,
        deadline = validItem.deadline,
        dateAdded = validItem.dateAddedRaw,
        description = validItem.description,
        sectorName = validItem.sectorName,
        genderName = validItem.genderName,
        contractTypeName = validItem.contractTypeName,
        workModeName = validItem.workModeName,
        authorEmail = validItem.authorEmail,
        authorWebsite = validItem.authorWebsite,
        authorMobile1 = validItem.authorMobile1,
        authorMobile2 = validItem.authorMobile2,
        authorLongitude = validItem.authorLongitude,
        authorLatitude = validItem.authorLatitude,
        company = validItem.company,
        companyLogoUrl = validItem.companyLogoUrl,
        salary = validItem.salary,
        experience = validItem.experience,
        educationLevel = validItem.educationLevel
    )
}


