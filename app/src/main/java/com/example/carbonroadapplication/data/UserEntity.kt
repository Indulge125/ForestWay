package com.example.carbonroadapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val username: String,
    val passwordHash: String, // 简单存储明文或哈希，比赛演示建议明文或Base64
    val nickname: String? = null,
    val registerTime: Long = System.currentTimeMillis()
)
