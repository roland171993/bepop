package com.stopgalere.domain.validation

/**
 * Business rules for accepting an API job.
 *
 * Rules:
 * - reject if the whole object (or any required field) is null
 * - title, date, city must be non-empty after trim
 * - title, date, city length must be in [3, 225]
 * - date must contain ONLY digits and spaces (e.g., "12 08 2025")
 */
object JobValidation {

    // Only numbers and spaces
    private val dateNumSpaceRegex = Regex("^[0-9 ]+$")

    fun isValid(
        title: String?,
        city: String?,
        date: String?
    ): Boolean {
        // Null object / fields
        val t = title?.trim() ?: return false
        val c = city?.trim() ?: return false
        val d = date?.trim() ?: return false

        // Empty or length out of range
        if (t.isEmpty() || c.isEmpty() || d.isEmpty()) return false
        if (t.length !in 3..225) return false
        if (c.length !in 3..225) return false
        if (d.length !in 3..225) return false

        // Date format strictly: digits and spaces only
        if (!dateNumSpaceRegex.matches(d)) return false

        return true
    }
}
