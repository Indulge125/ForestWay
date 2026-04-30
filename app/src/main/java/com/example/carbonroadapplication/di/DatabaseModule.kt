package com.example.carbonroadapplication.di

import android.content.Context
import androidx.room.Room
import com.example.carbonroadapplication.data.AppDatabase
import com.example.carbonroadapplication.data.TripDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "eco_travel.db"
        )
        // 【新增】添加迁移策略
        .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3, AppDatabase.MIGRATION_3_4) // 【修改】加入 MIGRATION_3_4
        .build()
    }

    @Provides
    fun provideTripDao(db: AppDatabase): TripDao {
        return db.tripDao()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): com.example.carbonroadapplication.data.UserDao {
        return db.userDao()
    }
}