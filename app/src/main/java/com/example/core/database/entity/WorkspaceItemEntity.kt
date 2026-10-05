package com.example.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workspace_items")
data class WorkspaceItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val stage: String, // IDEA, SCRIPT, RECORDING, EDITING, THUMBNAIL, SCHEDULED, PUBLISHED
    val type: String,  // LONG_VIDEO, SHORTS, SERIES, TUTORIAL, EVERGREEN
    val dueDate: String,
    val targetPublishDate: String,
    val notes: String = "",
    val tags: String = "", // Comma-separated tags
    val xpReward: Int = 50,
    val createdAt: Long = System.currentTimeMillis()
)
