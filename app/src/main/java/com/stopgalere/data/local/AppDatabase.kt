package com.stopgalere.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stopgalere.data.local.dao.JobDao
import com.stopgalere.data.local.dao.JobRemoteKeysDao
import com.stopgalere.data.model.JobEntity
import com.stopgalere.data.model.JobRemoteKeys

@Database(
    entities = [JobEntity::class, JobRemoteKeys::class],
    version = 1, exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
    abstract fun jobRemoteKeysDao(): JobRemoteKeysDao
}
