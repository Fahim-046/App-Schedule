package com.example.appalarm.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Task_table")

data class TaskInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "start_time") val startTime: Long,
    @ColumnInfo(name = "completed") val isCompleted: Boolean = false
)
