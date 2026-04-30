package com.example.carbonroadapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// 【修改】版本号升为 3
@Database(entities = [TripEntity::class, UserEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun userDao(): UserDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE trips ADD COLUMN username TEXT NOT NULL DEFAULT '23wlw34'")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE trips ADD COLUMN screenshotPath TEXT")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `users` (`username` TEXT NOT NULL, `passwordHash` TEXT NOT NULL, `nickname` TEXT, `registerTime` INTEGER NOT NULL, PRIMARY KEY(`username`))")
            }
        }
    }
}