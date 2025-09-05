package com.stopgalere.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CoverLetterDto(
    @SerializedName("_id") val id: String? = null,
    val title: String? = null,
    val content: String? = null,
    val createdAt: String? = null,
)

data class CoverLetterPagination(
    val total: Int? = null,
    val limit: Int? = null,
    val currentPage: Int? = null,
    val lastPage: Int? = null,
    val previousPage: Int? = null,
    val nextPage: Int? = null
)

data class CoverLettersResponse(
    val coverLetters: List<CoverLetterDto> = emptyList(),
    val pagination: CoverLetterPagination? = null
)



