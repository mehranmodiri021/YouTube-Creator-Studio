package com.example.core.database

import android.content.Context
import com.example.core.database.dao.ChannelDao
import com.example.core.database.dao.GoalDao
import com.example.core.database.dao.ScriptDao
import com.example.core.database.dao.WorkspaceDao

object DatabaseModule {
    private var database: CreatorDatabase? = null

    fun initialize(context: Context) {
        if (database == null) {
            database = CreatorDatabase.getInstance(context)
        }
    }

    val db: CreatorDatabase?
        get() = database

    val workspaceDao: WorkspaceDao?
        get() = database?.workspaceDao()

    val scriptDao: ScriptDao?
        get() = database?.scriptDao()

    val channelDao: ChannelDao?
        get() = database?.channelDao()

    val goalDao: GoalDao?
        get() = database?.goalDao()
}
