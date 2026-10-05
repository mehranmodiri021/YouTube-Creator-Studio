package com.example.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.core.database.dao.ChannelDao
import com.example.core.database.dao.GoalDao
import com.example.core.database.dao.ScriptDao
import com.example.core.database.dao.WorkspaceDao
import com.example.core.database.entity.ChannelEntity
import com.example.core.database.entity.GoalEntity
import com.example.core.database.entity.ScriptDraftEntity
import com.example.core.database.entity.WorkspaceItemEntity

@Database(
    entities = [
        WorkspaceItemEntity::class,
        ScriptDraftEntity::class,
        ChannelEntity::class,
        GoalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CreatorDatabase : RoomDatabase() {

    abstract fun workspaceDao(): WorkspaceDao
    abstract fun scriptDao(): ScriptDao
    abstract fun channelDao(): ChannelDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: CreatorDatabase? = null

        fun getInstance(context: Context): CreatorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CreatorDatabase::class.java,
                    "youtube_creator_studio.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
