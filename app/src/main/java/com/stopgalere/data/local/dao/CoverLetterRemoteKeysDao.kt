package com.stopgalere.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stopgalere.data.model.CoverLetterRemoteKeys

@Dao
interface CoverLetterRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<CoverLetterRemoteKeys>)

    // convenient single insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(remoteKey: CoverLetterRemoteKeys)

    @Query("SELECT * FROM cover_letter_remote_keys WHERE coverLetterId = :id")
    suspend fun remoteKeysById(id: String): CoverLetterRemoteKeys?

    @Query("DELETE FROM cover_letter_remote_keys")
    suspend fun clearKeys()

    // ---- Global nextKey fallback (same idea as Job) ----
    @Query("SELECT nextKey FROM cover_letter_remote_keys WHERE coverLetterId = 'GLOBAL' LIMIT 1")
    suspend fun globalNextKey(): Int?

    @androidx.room.Transaction
    suspend fun upsertGlobal(nextKey: Int?) {
        if (nextKey != null) {
            insertOne(
                CoverLetterRemoteKeys(
                    coverLetterId = "GLOBAL",
                    prevKey = null,
                    nextKey = nextKey
                )
            )
        }
    }

    @Query("DELETE FROM cover_letter_remote_keys WHERE coverLetterId = 'GLOBAL'")
    suspend fun clearGlobal()
}
