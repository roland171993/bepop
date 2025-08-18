package com.stopgalere.presentation.ui.job

import androidx.compose.runtime.Immutable
import com.stopgalere.domain.model.Job

@Immutable
data class JobUi(
    val id: String,
    val title: String,
    val city: String,
    val date: String
)

fun Job.toUi(): JobUi = JobUi(
    id = id,
    title = title,
    city = city ?: "—",
    date = date ?: "—"
)
