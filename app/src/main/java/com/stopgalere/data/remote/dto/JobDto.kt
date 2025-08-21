package com.stopgalere.data.remote.dto

import com.google.gson.annotations.SerializedName

data class JobDto(
    // _id dont meet kotlin coding convention
    @SerializedName("_id") val id: String?,
    val title: String?,
    val city: String?,
    @SerializedName("dateAdded") val dateAdded: String?
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
