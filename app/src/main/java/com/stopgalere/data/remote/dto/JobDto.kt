package com.stopgalere.data.remote.dto

data class JobDto(
    val id: String,
    val title: String,
    val city: String?,
    val date: String?
)
data class JobPageDto(
    val items: List<JobDto>,
    val nextPage: Int?, // null when no next page
    val prevPage: Int?  // null when no prev page
)
