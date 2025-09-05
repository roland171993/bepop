package com.stopgalere.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.stopgalere.data.local.dao.CoverLetterDao
import com.stopgalere.data.local.dao.CoverLetterRemoteKeysDao
import com.stopgalere.data.local.dao.JobDao
import com.stopgalere.data.local.dao.JobRemoteKeysDao
import com.stopgalere.data.model.CoverLetterEntity
import com.stopgalere.data.model.CoverLetterRemoteKeys
import com.stopgalere.data.model.JobEntity
import com.stopgalere.data.model.JobRemoteKeys

@Database(
    entities = [
        JobEntity::class,
        JobRemoteKeys::class,
        CoverLetterEntity::class,
        CoverLetterRemoteKeys::class
               ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
    abstract fun jobRemoteKeysDao(): JobRemoteKeysDao

    abstract fun coverLetterDao(): CoverLetterDao
    abstract fun coverLetterRemoteKeysDao(): CoverLetterRemoteKeysDao
}
