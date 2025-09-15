package com.stopgalere.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.stopgalere.data.model.JobRemoteKeys



@Dao
interface JobRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<JobRemoteKeys>)

    // Convenient single insert for the global row
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(remoteKey: JobRemoteKeys)

    @Query("SELECT * FROM job_remote_keys WHERE jobId = :jobId LIMIT 1")
    suspend fun remoteKeysById(jobId: String): JobRemoteKeys?

    @Query("DELETE FROM job_remote_keys")
    suspend fun clearKeys()

    /*
    BUG FIXED
    So the root cause really was:

    Unstable ordering in your JobDao paging query, and

    Missing fallback nextKey for first-run APPEND in the mediator.

    With the deterministic ORDER BY and the global nextKey row, Paging always knows how to fetch the next page on first launch.

    Explanation: Paging 3 gotcha: placeholders + DB-backed PagingSource can freeze pagination when the DB’s count equals the currently cached rows, because Paging thinks that is the full dataset.
    */

    // ---- Global nextKey fallback ----

    // Read the latest 'nextKey' we stored globally (if any).
    @Query("SELECT nextKey FROM job_remote_keys WHERE jobId = 'GLOBAL' LIMIT 1")
    suspend fun globalNextKey(): Int?

    // Upsert the global nextKey (only if non-null).
    @Transaction
    suspend fun upsertGlobal(nextKey: Int?) {
        if (nextKey != null) {
            insertOne(
                JobRemoteKeys(
                    jobId = "GLOBAL",
                    prevKey = null,
                    nextKey = nextKey
                )
            )
        }
    }

    @Query("DELETE FROM job_remote_keys WHERE jobId = 'GLOBAL'")
    suspend fun clearGlobal()
}
