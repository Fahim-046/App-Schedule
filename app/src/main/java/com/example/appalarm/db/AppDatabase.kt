package com.example.appalarm.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appalarm.models.TaskInfo

@Database(
    entities = [TaskInfo::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        operator fun invoke(context: Context) = buildDatabase(context)

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "TaskDatabase.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
