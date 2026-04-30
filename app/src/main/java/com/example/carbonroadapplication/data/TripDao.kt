package com.example.carbonroadapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert
    suspend fun insertTrip(trip: TripEntity)

    // 【修改】只查询当前用户的行程
    @Query("SELECT * FROM trips WHERE username = :username ORDER BY timestamp DESC")
    fun getAllTrips(username: String): Flow<List<TripEntity>>
    
    // 【修改】只统计当前用户的减排
    @Query("SELECT COALESCE(SUM(carbonSaved), 0.0) FROM trips WHERE username = :username")
    fun getTotalCarbon(username: String): Flow<Double>

    // 【修改】只统计当前用户的里程
    @Query("SELECT COALESCE(SUM(distanceMeters), 0.0) FROM trips WHERE username = :username")
    fun getTotalDistance(username: String): Flow<Double>
    
    // 【修改】只统计当前用户的次数
    @Query("SELECT COUNT(*) FROM trips WHERE username = :username")
    fun getTripCount(username: String): Flow<Int>
}