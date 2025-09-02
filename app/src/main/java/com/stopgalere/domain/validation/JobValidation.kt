package com.stopgalere.domain.validation

import com.stopgalere.domain.validation.SafeText.isSafeText
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
     * High-level object validation (DOMAIN). This does not mutate inputs.
     * Use validateAndFormatDate(...) to obtain the normalized date string.
     */
    fun isValid(
        title: String?,
        city: String?,
        dateAdded: String?
    ): Boolean {
        val t = title?.trim() ?: return false
        val c = city?.trim() ?: return false
        val d = dateAdded?.trim() ?: return false

        if (t.isEmpty() || c.isEmpty() || d.isEmpty()) return false
        if (t.length !in 3..225) return false
        if (c.length !in 3..225) return false
        if (d.length !in 3..225) return false

        return true
    }

    fun validateAndFormatMoney(salary: Int?): String {
        return when {
            salary == null || salary <= 0 -> "Salaire non renseigné"
            else -> String.format(Locale.FRANCE,"%,d FCFA", salary).replace(',', ' ')
        }
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

    /** Policy for skipping based on description/sector/company. */
    fun shouldSkipByPolicy(description: String?, sectorName: String?, company: String?): Boolean {
        val d = description?.trim()
        val s = sectorName?.trim()
        val c = company?.trim()

        // null / empty
        if (d.isNullOrEmpty() || s.isNullOrEmpty() || c.isNullOrEmpty()) return true

        // length rules
        if (d.length !in 3..225) return true
        if (s.length !in 3..225) return true
        if (c.length !in 2..225) return true

        // regex safety
        if (!isSafeText(d) || !isSafeText(s) || !isSafeText(c)) return true

        return false
    }

    /**
     * Single-entry validator for jobs used by the DATA layer.
     *
     * Returns the normalized "dd-MM-yyyy" date if and only if ALL constraints pass:
     *  - title/city/date basic rules
     *  - date parsing/normalization
     *  - policy rules on description/sector/company
     *  - regex/allowlist gate on all provided fields
     *
     * If anything fails, returns null.
     *
     * Usage:
     *   val normalized = JobValidation.validateAll(
     *       title = ...,
     *       city = ...,
     *       dateRaw = ...,
     *       description = ...,
     *       sectorName = ...,
     *       company = ...,
     *       // any additional fields that should pass SafeText gate
     *       extraSafeFields = listOf(...)
     *   )
     */
    fun validateAll(
        title: String?,
        city: String?,
        dateRaw: String?,
        deadlineRaw: String?,
        description: String?,
        sectorName: String?,
        company: String?,
        salary: Int? = null,
        gate: List<String?> = emptyList()
    ): String? {
        // Step 1: high-level shape validation + date presence
        if (!isValid(title, city, dateRaw)) return null

        // Step 2: normalize date to dd-MM-yyyy (also re-parses defensively)
        val normalizedDate = validateAndFormatDate(dateRaw) ?: return null

        if (salary == null) return null

        // Step 2: normalize date to dd-MM-yyyy (also re-parses defensively)
        if (validateAndFormatDate(deadlineRaw).isNullOrEmpty()) return null

        // Step 3: business/policy skip criteria
        if (shouldSkipByPolicy(description, sectorName, company)) return null

        // Step 4: SafeText gate on all relevant fields (including normalized date)
        if (gate.any { !isSafeText(it) }) return null

        return normalizedDate
    }

}

