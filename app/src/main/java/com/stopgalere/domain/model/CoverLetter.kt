package com.stopgalere.domain.model

/*
  keep normalized "date" for UI + raw "dateAdded" for sort.
 */
data class CoverLetter(
    val id: String,
    val title: String,
    val content: String,

    // dates
    val date: String,       // dd-MM-yyyy (from createdAt)
    val dateAdded: String,  // createdAt (raw ISO)
)
