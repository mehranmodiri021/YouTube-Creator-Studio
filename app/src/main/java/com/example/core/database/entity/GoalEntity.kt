package com.example.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val targetType: String, // SUBSCRIBERS, WATCH_HOURS, VIDEOS, SHORTS
    val currentValue: Long,
    val targetValue: Long,
    val unit: String,
    val deadline: String,
    val isCompleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
