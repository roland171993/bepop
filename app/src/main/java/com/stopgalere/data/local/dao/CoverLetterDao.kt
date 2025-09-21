package com.stopgalere.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stopgalere.data.model.CoverLetterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoverLetterDao {

    @Query("""
        SELECT * FROM cover_letters
        WHERE (:query IS NULL OR 
               title LIKE '%' || :query || '%' OR
               content LIKE '%' || :query || '%')
        ORDER BY 
          CASE 
            WHEN dateAdded IS NOT NULL AND dateAdded != '' THEN dateAdded
            ELSE (substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2))
          END DESC,
          id ASC   -- Deterministic tie-breaker to keep last-item stable for Paging
    """)
    fun pagingSource(query: String?): PagingSource<Int, CoverLetterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<CoverLetterEntity>)

    @Query("DELETE FROM cover_letters")
    suspend fun clearAll()

    @Query("SELECT * FROM cover_letters WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<CoverLetterEntity?>
}
