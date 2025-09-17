package com.stopgalere.domain.validation

import com.stopgalere.data.remote.dto.JobDto
import com.stopgalere.domain.validation.common.DateValidation.validateAndFormatDate
import com.stopgalere.domain.validation.common.SafeText.isSafeText
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

    /*fun validate(dto: JobDto): ValidJob? {
        val id = dto.id?.trim() ?: return null
        val title = dto.title?.trim().takeIf { !it.isNullOrEmpty() && it.length in 3..225 } ?: return null
        val city  = dto.city?.trim().takeIf { !it.isNullOrEmpty() && it.length in 3..225 } ?: return null

        val dateRaw = dto.dateAdded?.trim() ?: return null
        val date = validateAndFormatDate(dateRaw) ?: return null
        val deadline = validateAndFormatDate(dto.deadline?.trim()) ?: return null

        // required-ish content
        val description = dto.description?.trim().orEmpty()
        val sectorName  = dto.sector?.name?.trim().orEmpty()
        val company     = dto.company?.trim().orEmpty()

        if (description.isEmpty() || sectorName.isEmpty() || company.isEmpty()) return null

        // safe-text allowlist on critical strings
        val gate = listOf(
            title, city, description, sectorName, company,
            dto.gender?.name, dto.contractType?.name, dto.workMode?.name,
            dto.authorEmail, dto.authorWebsite, dto.authorMobile1, dto.authorMobile2,
            dto.companyLogoUrl, dto.experience, dto.educationLevel
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
            genderName = dto.gender?.name?.trim().orEmpty(),
            contractTypeName = dto.contractType?.name?.trim().orEmpty(),
            workModeName = dto.workMode?.name?.trim().orEmpty(),
            authorEmail = dto.authorEmail?.trim().orEmpty(),
            authorWebsite = dto.authorWebsite?.trim().orEmpty(),
            authorMobile1 = dto.authorMobile1?.trim().orEmpty(),
            authorMobile2 = dto.authorMobile2?.trim().orEmpty(),
            authorLongitude = dto.authorLongitude,
            authorLatitude = dto.authorLatitude,
            company = company,
            companyLogoUrl = dto.companyLogoUrl?.trim().orEmpty(),
            salary = validateAndFormatMoney(dto.salary),
            experience = dto.experience?.trim().orEmpty(),
            educationLevel = dto.educationLevel?.trim().orEmpty()
        )
    }*/

    fun validate(dto: JobDto): ValidJob? {
        val dateRaw = dto.dateAdded?.trim() ?: return null
        val date = validateAndFormatDate(dateRaw) ?: return null
        val deadline = validateAndFormatDate(dto.deadline?.trim()) ?: return null

        println("SEARCH validate will ok")

        return ValidJob(
            id = dto.id!!,
            title = dto.title!!,
            city = dto.city!!,
            date = date,
            deadline = deadline,
            dateAddedRaw = dateRaw,
            description = dto.description!!,
            sectorName = dto.sector?.name!!,
            genderName = dto.gender?.name?.trim().orEmpty(),
            contractTypeName = dto.contractType?.name?.trim().orEmpty(),
            workModeName = dto.workMode?.name?.trim().orEmpty(),
            authorEmail = dto.authorEmail?.trim().orEmpty(),
            authorWebsite = dto.authorWebsite?.trim().orEmpty(),
            authorMobile1 = dto.authorMobile1?.trim().orEmpty(),
            authorMobile2 = dto.authorMobile2?.trim().orEmpty(),
            authorLongitude = dto.authorLongitude,
            authorLatitude = dto.authorLatitude,
            company = dto.company!!,
            companyLogoUrl = dto.companyLogoUrl?.trim().orEmpty(),
            salary = validateAndFormatMoney(dto.salary),
            experience = dto.experience?.trim().orEmpty(),
            educationLevel = dto.educationLevel?.trim().orEmpty()
        )
    }

    fun validateAndFormatMoney(salary: Int?): String =
        if (salary == null || salary <= 0) "Salaire non renseigné"
        else String.format(Locale.FRANCE, "%,d FCFA", salary).replace(',', ' ')
}
