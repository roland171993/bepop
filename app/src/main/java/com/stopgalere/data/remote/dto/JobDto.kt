package com.stopgalere.data.remote.dto

import com.google.gson.annotations.SerializedName

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
    val authorLongitude: String? = null,
    val authorLatitude: String? = null,
    val company: String? = null,
    val companyLogoUrl: String? = null,

    // misc
    val salary: String? = null,
    val city: String? = null,
    val experience: String? = null,
    val educationLevel: String? = null,

    // timestamps we keep
    @SerializedName("dateAdded") val dateAdded: String? = null,
    val updatedAt: String? = null

    // skipped from JSON: __v, deadline, createdAt, unpublished
)

data class JobsResponse(
    val jobs: List<JobDto> = emptyList(),
    val pagination: Pagination? = null
)

data class Pagination(
    val total: Int? = null,
    val limit: Int? = null,
    val currentPage: Int? = null,
    val lastPage: Int? = null,
    val previousPage: Int? = null,
    val nextPage: Int? = null
)
