package com.example.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "script_drafts")
data class ScriptDraftEntity(
    @PrimaryKey val id: String,
    val title: String,
    val topic: String,
    val hook: String,
    val intro: String,
    val mainContentJson: String, // serialized sections
    val storyElement: String,
    val cta: String,
    val outro: String,
    val estimatedMinutes: Int = 5,
    val updatedAt: Long = System.currentTimeMillis()
)
