package com.stopgalere.domain.repository

import androidx.paging.PagingData
import com.stopgalere.domain.model.CoverLetter
import kotlinx.coroutines.flow.Flow

interface CoverLetterRepoInterface {
    fun getCoverLetters(query: String?, online: Boolean): Flow<PagingData<CoverLetter>>
    fun coverLetterById(id: String): Flow<CoverLetter?>
}
