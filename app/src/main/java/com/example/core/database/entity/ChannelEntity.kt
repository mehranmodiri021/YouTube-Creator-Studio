package com.example.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey val id: String,
    val title: String,
    val handle: String,
    val description: String,
    val avatarUrl: String,
    val bannerUrl: String,
    val subscriberCount: Long,
    val viewCount: Long,
    val videoCount: Long,
    val shortsCount: Long,
    val category: String,
    val healthScore: Int,
    val dailyGrowth: Long,
    val weeklyGrowth: Long,
    val monthlyGrowth: Long,
    val isMonetized: Boolean,
    val totalWatchHours: Double,
    val isActive: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
