package com.example.core.database.dao

import androidx.room.*
import com.example.core.database.entity.WorkspaceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {

    @Query("SELECT * FROM workspace_items ORDER BY createdAt DESC")
    fun getAllWorkspaceItems(): Flow<List<WorkspaceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: WorkspaceItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WorkspaceItemEntity>)

    @Update
    suspend fun updateItem(item: WorkspaceItemEntity)

    @Query("UPDATE workspace_items SET stage = :stage WHERE id = :id")
    suspend fun updateStage(id: String, stage: String)

    @Delete
    suspend fun deleteItem(item: WorkspaceItemEntity)

    @Query("DELETE FROM workspace_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM workspace_items")
    suspend fun count(): Int
}
