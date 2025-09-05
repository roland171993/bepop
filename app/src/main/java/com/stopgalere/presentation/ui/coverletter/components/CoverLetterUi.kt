package com.stopgalere.presentation.ui.coverletter.components

import androidx.compose.runtime.Immutable
import com.stopgalere.domain.model.CoverLetter

@Immutable
data class CoverLetterUi(
    val id: String,
    val title: String,
    val content: String,

    // dates
    val date: String,       // normalized dd-MM-yyyy (from createdAt)
    val dateAdded: String   // raw ISO, useful for sorting
)

// Mapper from domain to UI
fun CoverLetter.toUi(): CoverLetterUi = CoverLetterUi(
    id = id,
    title = title.ifBlank { "—" },
    content = content.ifBlank { "—" },
    date = date.ifBlank { "—" },
    dateAdded = dateAdded
)
