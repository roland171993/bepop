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

    @Query("SELECT * FROM cover_letter_remote_keys WHERE coverLetterId = :id")
    suspend fun remoteKeysById(id: String): CoverLetterRemoteKeys?

    @Query("DELETE FROM cover_letter_remote_keys")
    suspend fun clearKeys()
}
