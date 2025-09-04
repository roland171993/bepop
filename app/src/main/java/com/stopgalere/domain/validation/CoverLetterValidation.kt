package com.stopgalere.domain.validation

import com.stopgalere.domain.validation.SafeText.isSafeText
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

object CoverLetterValidation {


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
     * Validates mandatory fields and returns UI date (dd-MM-yyyy) from createdAt.
     * Returns null to drop the item if critical fields are invalid.
     */
    fun validateAll(
        title: String?,
        content: String?,
        createdAtRaw: String?,
        gate: List<String?>
    ): String? {
        if (title.isNullOrBlank() || content.isNullOrBlank() || createdAtRaw.isNullOrBlank())
            return null
        if (gate.any { !isSafeText(it) }) return null
        return validateAndFormatDate(createdAtRaw)
    }

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
}
