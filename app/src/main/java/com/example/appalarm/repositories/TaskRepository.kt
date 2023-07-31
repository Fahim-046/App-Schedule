package com.example.appalarm.repositories

import com.example.appalarm.db.AppDatabase
import com.example.appalarm.models.TaskInfo
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository @Inject constructor(private val db: AppDatabase) {

    suspend fun addTask(task: TaskInfo) {
        return withContext(Dispatchers.IO) {
            db.taskDao().insert(task)
        }
    }

    suspend fun checkSameTime(time: Long): List<TaskInfo> {
        return withContext(Dispatchers.IO) {
            db.taskDao().check(time)
        }
    }

    suspend fun getTask(): List<TaskInfo> {
        return withContext(Dispatchers.IO) {
            db.taskDao().getAllTask()
        }
    }

    suspend fun deleteTask(task: TaskInfo) {
        return withContext(Dispatchers.IO) {
            db.taskDao().delete(task)
        }
    }

    suspend fun updateTask(task: TaskInfo) {
        return withContext(Dispatchers.IO) {
            db.taskDao().update(task)
        }
    }

    suspend fun getTaskByTime(time: Long): TaskInfo {
        return withContext(Dispatchers.IO) {
            db.taskDao().getTaskByTime(time)
        }
    }

    suspend fun getTaskById(taskId: Int): TaskInfo {
        return withContext(Dispatchers.IO) {
            db.taskDao().getTaskById(taskId)
        }
    }
}
