package com.example.core.database.dao

import androidx.room.*
import com.example.core.database.entity.ScriptDraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptDao {

    @Query("SELECT * FROM script_drafts ORDER BY updatedAt DESC")
    fun getAllScripts(): Flow<List<ScriptDraftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScript(script: ScriptDraftEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scripts: List<ScriptDraftEntity>)

    @Update
    suspend fun updateScript(script: ScriptDraftEntity)

    @Delete
    suspend fun deleteScript(script: ScriptDraftEntity)

    @Query("SELECT COUNT(*) FROM script_drafts")
    suspend fun count(): Int
}
