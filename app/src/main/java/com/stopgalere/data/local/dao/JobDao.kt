package com.stopgalere.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stopgalere.data.model.JobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {

    @Query("""
        SELECT * FROM jobs
        WHERE (:query IS NULL OR title LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%')
        ORDER BY 
          -- Prefer server-provided dateAdded when present, otherwise fallback to parsed legacy 'date'
          CASE 
            WHEN dateAdded IS NOT NULL AND dateAdded != '' THEN dateAdded
            ELSE (substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2))
          END DESC,
          -- Deterministic tie-breaker to keep last-item stable for Paging
          id ASC
    """)
    fun pagingSource(query: String?): PagingSource<Int, JobEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<JobEntity>)

    @Query("DELETE FROM jobs")
    suspend fun clearAll()

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<JobEntity?>
}
