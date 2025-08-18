package com.stopgalere.data.remote

import com.stopgalere.data.remote.dto.JobPageDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // Example: /jobs?page=1&pageSize=20&query=android
    @GET("jobs")
    suspend fun getJobs(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("query") query: String?
    ): JobPageDto
}
