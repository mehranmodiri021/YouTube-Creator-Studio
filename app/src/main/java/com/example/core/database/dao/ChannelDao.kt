package com.example.core.database.dao

import androidx.room.*
import com.example.core.database.entity.ChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Query("SELECT * FROM channels ORDER BY subscriberCount DESC")
    fun getAllChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE isActive = 1 LIMIT 1")
    fun getActiveChannel(): Flow<ChannelEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: ChannelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(channels: List<ChannelEntity>)

    @Query("UPDATE channels SET isActive = 0")
    suspend fun clearActiveChannels()

    @Query("UPDATE channels SET isActive = 1 WHERE id = :channelId")
    suspend fun setActiveChannel(channelId: String)

    @Delete
    suspend fun deleteChannel(channel: ChannelEntity)

    @Query("SELECT COUNT(*) FROM channels")
    suspend fun count(): Int
}
