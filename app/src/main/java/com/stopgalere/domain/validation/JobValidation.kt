package com.stopgalere.domain.validation

import com.stopgalere.data.remote.dto.JobDto
import com.stopgalere.domain.validation.common.DateValidation.validateAndFormatDate
import com.stopgalere.domain.validation.common.SafeText.isSafeText
import com.stopgalere.util.AppConstants.STRING_LENGTH_MAX
import com.stopgalere.util.AppConstants.STRING_LENGTH_MIN
import java.util.Locale

/**
 * Single-entry validator for Job DTOs used by the DATA layer.
 * Returns a normalized snapshot if everything passes; otherwise null.
 */
object JobValidation {

    data class ValidJob(
        val id: String,
        val title: String,
        val city: String,
        val date: String,          // dd-MM-yyyy
        val deadline: String,      // dd-MM-yyyy
        val description: String,
        val sectorName: String,
        val genderName: String,
        val contractTypeName: String,
        val workModeName: String,
        val authorEmail: String,
        val authorWebsite: String,
        val authorMobile1: String,
        val authorMobile2: String,
        val authorLongitude: Double?,
        val authorLatitude: Double?,
        val company: String,
        val companyLogoUrl: String,
        val salary: String,        // formatted
        val experience: String,
        val educationLevel: String,
        val dateAddedRaw: String   // keep for Room sorting
    )

    fun validate(dto: JobDto): ValidJob? {
        val id = dto.id?.trim() ?: return null

        val title = dto.title?.trim().takeIf { !it.isNullOrEmpty() && it.length in STRING_LENGTH_MIN..STRING_LENGTH_MAX } ?: return null
        val city  = dto.city?.trim().takeIf { !it.isNullOrEmpty() && it.length in STRING_LENGTH_MIN..STRING_LENGTH_MAX } ?: return null

        // date fields are strings in the DTO; enforce length first, then format
        val dateRaw = dto.dateAdded?.trim()?.takeIf { it.length in STRING_LENGTH_MIN..STRING_LENGTH_MAX } ?: return null
        val deadlineRaw = dto.deadline?.trim()?.takeIf { it.length in STRING_LENGTH_MIN..STRING_LENGTH_MAX } ?: return null

        val date = validateAndFormatDate(dateRaw) ?: return null
        val deadline = validateAndFormatDate(deadlineRaw) ?: return null

        // required-ish content + new length constraints
        val description = dto.description?.trim()
            ?.takeIf { it.length in STRING_LENGTH_MIN..5000 } ?: return null  // Avoid ManInTheMiddle attacks limit string length

        val sectorName = dto.sector?.name?.trim()
            ?.takeIf { it.length in STRING_LENGTH_MIN..STRING_LENGTH_MAX } ?: return null

        val company = dto.company?.trim()
            ?.takeIf { it.length in 2..STRING_LENGTH_MAX } ?: return null

        // Optional/nested strings normalized
        val genderName        = dto.gender?.name?.trim().orEmpty()
        val contractTypeName  = dto.contractType?.name?.trim().orEmpty()
        val workModeName      = dto.workMode?.name?.trim().orEmpty()
        val authorEmail       = dto.authorEmail?.trim().orEmpty()
        val authorWebsite     = dto.authorWebsite?.trim().orEmpty()
        val authorMobile1     = dto.authorMobile1?.trim().orEmpty()
        val authorMobile2     = dto.authorMobile2?.trim().orEmpty()
        val companyLogoUrl    = dto.companyLogoUrl?.trim().orEmpty()
        val experience        = dto.experience?.trim().orEmpty()
        val educationLevel    = dto.educationLevel?.trim().orEmpty()

        //  GATE: must contain ALL string fields (except longitude, latitude, salary)
        val gate = listOf(
            id,
            title,
            description,
            sectorName,
            genderName,
            contractTypeName,
            workModeName,
            authorEmail,
            authorWebsite,
            authorMobile1,
            authorMobile2,
            company,
            companyLogoUrl,
            city,
            experience,
            educationLevel,
            dateRaw,       // original strings before formatting
            deadlineRaw
        )
        if (gate.any { !isSafeText(it) }) return null

        return ValidJob(
            id = id,
            title = title,
            city = city,
            date = date,
            deadline = deadline,
            dateAddedRaw = dateRaw,
            description = description,
            sectorName = sectorName,
            genderName = genderName,
            contractTypeName = contractTypeName,
            workModeName = workModeName,
            authorEmail = authorEmail,
            authorWebsite = authorWebsite,
            authorMobile1 = authorMobile1,
            authorMobile2 = authorMobile2,
            authorLongitude = dto.authorLongitude,
            authorLatitude = dto.authorLatitude,
            company = company,
            companyLogoUrl = companyLogoUrl,
            salary = validateAndFormatMoney(dto.salary),
            experience = experience,
            educationLevel = educationLevel
        )
    }

    fun validateAndFormatMoney(salary: Int?): String =
        if (salary == null || salary <= 0) "Salaire non renseigné"
        else String.format(Locale.FRANCE, "%,d FCFA", salary).replace(',', ' ')
}
