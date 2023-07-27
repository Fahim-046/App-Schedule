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

    suspend fun checkTask(time: Long): TaskInfo? {
        return withContext(Dispatchers.IO) {
            db.taskDao().checkTask(time, false)
        }
    }
}
