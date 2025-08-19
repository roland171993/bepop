package com.stopgalere.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stopgalere.data.model.JobRemoteKeys

@Dao
interface JobRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<JobRemoteKeys>)

    @Query("SELECT * FROM job_remote_keys WHERE jobId = :jobId")
    suspend fun remoteKeysById(jobId: String): JobRemoteKeys?

    @Query("DELETE FROM job_remote_keys")
    suspend fun clearKeys()
}
