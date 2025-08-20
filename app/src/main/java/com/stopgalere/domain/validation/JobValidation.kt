package com.stopgalere.domain.validation

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * DOMAIN layer: Business rules + normalization for Job input.
 *
 * Responsibilities:
 * - Validate title/city/date basic constraints (null/length)
 * - Validate acceptable date inputs
 * - Normalize date to French "dd-MM-yyyy"
 *
 * Acceptable date inputs:
 *   - ISO instant:       2025-08-19T10:23:55.338Z
 *   - ISO local date:    2025-08-19
 *   - French date:       19-08-2025
 *
 * Note: uses java.text.* so it works on API 24+ without desugaring.
 */
object JobValidation {

    // Policy rule (post-normalization check)
    private val dateNumSpaceDashRegex = Regex("^[0-9\\- ]+$")

    // Strict parsers/formatters
    private val sdfIsoLocal = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        isLenient = false
    }
    private val sdfFrench = SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE).apply {
        isLenient = false
    }

    /**
     * Try to parse various inputs and return normalized "dd-MM-yyyy"
     * Returns null if parsing fails.
     */
    fun validateAndFormatDate(raw: String?): String? {
        if (raw == null) return null
        val d = raw.trim()
        if (d.isEmpty()) return null

        // 1) ISO instant: 2025-08-19T10:23:55.338Z (UTC)
        if (d.contains("T") && d.endsWith("Z")) {
            try {
                val isoInstant = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                    isLenient = false
                }
                val date = isoInstant.parse(d)
                val out = sdfFrench.format(date)
                if (!dateNumSpaceDashRegex.matches(out)) return null
                return out
            } catch (_: ParseException) { /* try next */ }
        }

        // 2) ISO local date: 2025-08-19
        try {
            val date = sdfIsoLocal.parse(d)
            val out = sdfFrench.format(date)
            if (!dateNumSpaceDashRegex.matches(out)) return null
            return out
        } catch (_: ParseException) { /* try next */ }

        // 3) Already French: 19-08-2025
        try {
            val date = sdfFrench.parse(d)
            val out = sdfFrench.format(date)
            if (!dateNumSpaceDashRegex.matches(out)) return null
            return out
        } catch (_: ParseException) { /* all failed */ }

        return null
    }

    /**
     * High-level object validation (DOMAIN). This does not mutate inputs.
     * Use validateAndFormatDate(...) to obtain the normalized date string.
     */
    fun isValid(
        title: String?,
        city: String?,
        date: String?
    ): Boolean {
        val t = title?.trim() ?: return false
        val c = city?.trim() ?: return false
        val d = date?.trim() ?: return false

        if (t.isEmpty() || c.isEmpty() || d.isEmpty()) return false
        if (t.length !in 3..225) return false
        if (c.length !in 3..225) return false
        if (d.length !in 3..225) return false

        // Must be parsable and normalizable to dd-MM-yyyy
        val normalized = validateAndFormatDate(d) ?: return false

        // Optional: enforce policy regex on the normalized value (already done inside)
        if (!dateNumSpaceDashRegex.matches(normalized)) return false

        return true
    }
}
