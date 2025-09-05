package com.stopgalere.domain.validation

import com.stopgalere.domain.validation.common.SafeText.isSafeText
import com.stopgalere.domain.validation.common.DateValidation
import com.stopgalere.domain.validation.common.DateValidation.validateAndFormatDate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

object CoverLetterValidation {


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
        if (title.length !in 3..225) return null
        if (content.length !in 3..225) return null
        if (createdAtRaw.length !in 3..225) return null

        if (gate.any { !isSafeText(it) }) return null
        return validateAndFormatDate(createdAtRaw)
    }

}
