package com.stopgalere.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stopgalere.data.model.JobEntity

@Dao
interface JobDao {

    @Query("""
        SELECT * FROM jobs
        WHERE (:query IS NULL OR title LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%')
        ORDER BY COALESCE(dateAdded, date) DESC
    """)
    fun pagingSource(query: String?): PagingSource<Int, JobEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<JobEntity>)

    @Query("DELETE FROM jobs")
    suspend fun clearAll()
}
