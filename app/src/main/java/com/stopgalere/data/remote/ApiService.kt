package com.stopgalere.data.remote

import com.stopgalere.data.remote.dto.JobsResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * DATA layer – Retrofit interface for Jobs endpoints.
 *
 * Base URL should end with `/api/`, e.g.:
 *   http://192.168.1.12:3000/api/
 *
 * Example call:
 *   GET http://192.168.1.12:3000/api/jobs?page=1&limit=15
 *
 * Notes:
 * - `query` is nullable: when null, Retrofit omits it from the URL.
 * - Return type is JobsResponse, which includes:
 *     - jobs: List<JobDto>
 *     - pagination: { total, limit, currentPage, lastPage, previousPage, nextPage }
 */
interface ApiService {

    @DELETE("jobs/{id}")
    suspend fun deleteJob(@Path("id") id: String)

    @GET("jobs")
    suspend fun getJobs(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("query") query: String? = null
    ): JobsResponse
}