package com.example.carbonroadapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,      // 行程结束时间
    val distanceMeters: Double, // 里程 (米)
    val carbonSaved: Double,    // 减排 (kg)
    val durationSeconds: Long = 0, // (可选) 耗时
    // 【新增】用户名，用于区分数据归属
    val username: String,
    // 【新增】截图保存路径 (可为空，兼容老数据)
    val screenshotPath: String? = null
)