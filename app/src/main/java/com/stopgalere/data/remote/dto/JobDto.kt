package com.stopgalere.data.remote.dto


import com.google.gson.annotations.SerializedName

data class JobDto(
    @SerializedName("_id") val id: String?,
    val title: String?,
    val city: String?,
    val dateAdded: String?
)

/** The paged response shape from http://localhost:3000/api/jobs */
data class JobsResponse(
    val jobs: List<JobDto> = emptyList(),
    val pagination: Pagination? = null
)

data class Pagination(
    val total: Int? = null,
    val page: Int? = null,
    val totalPages: Int? = null,
    val limit: Int? = null
)