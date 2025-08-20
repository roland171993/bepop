package com.stopgalere.data.remote

import com.stopgalere.data.remote.dto.JobDto
import com.stopgalere.data.remote.dto.JobsResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    // http://localhost:3000/api/jobs?page=1&limit=15&query=android
    @GET("jobs")
    suspend fun getJobs(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("query") query: String? = null
    ): JobsResponse
}
