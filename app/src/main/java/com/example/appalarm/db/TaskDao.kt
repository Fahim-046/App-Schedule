package com.example.appalarm.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appalarm.models.TaskInfo

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskInfo): Long

    @Query("SELECT * FROM task_table WHERE start_time=:time")
    suspend fun check(time: Long): List<TaskInfo>

    @Query("SELECT * FROM task_table")
    suspend fun getAllTask(): List<TaskInfo>

    @Delete
    suspend fun delete(task: TaskInfo)

    @Update
    suspend fun update(task: TaskInfo)

    @Query("SELECT * FROM task_table WHERE start_time=:time")
    suspend fun getTaskByTime(time: Long): TaskInfo

    @Query("SELECT * FROM task_table WHERE completed=:status")
    suspend fun getUncompletedTask(status: Boolean): List<TaskInfo>
}
