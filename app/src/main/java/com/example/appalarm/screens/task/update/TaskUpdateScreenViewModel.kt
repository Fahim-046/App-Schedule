package com.example.appalarm.screens.task.update

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appalarm.AppOpenReceiver
import com.example.appalarm.models.TaskInfo
import com.example.appalarm.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class TaskUpdateScreenViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _task: MutableLiveData<TaskInfo> by lazy {
        MutableLiveData<TaskInfo>()
    }

    val task: LiveData<TaskInfo>
        get() = _task

    private val _message: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val message: LiveData<String>
        get() = _message

    private val _success: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val success: LiveData<Boolean>
        get() = _success

    private val _deleteSuccess: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val deleteSuccess: LiveData<Boolean>
        get() = _deleteSuccess

    fun getTask(
        taskId: Int
    ) = viewModelScope.launch {
        try {
            val response = repository.getTaskById(taskId)
            _task.value = response
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    fun deleteTask(
        task: TaskInfo
    ) = viewModelScope.launch {
        repository.deleteTask(task)
        _deleteSuccess.postValue(true)
    }

    private fun isValid(
        name: String
    ): Boolean {
        if (name.isBlank()) {
            return false
        }

        return true
    }

    @SuppressLint("ScheduleExactAlarm")
    fun updateTask(
        context: Context,
        id: Int,
        name: String,
        time: Long
    ) = viewModelScope.launch {
        val response = repository.checkSameTime(time)
        if (!isValid(name) && response.isEmpty()) {
            return@launch
        }
        if (response.isNotEmpty()) {
            _message.value = "Please set different time"
            return@launch
        }
        try {
            repository.updateTask(TaskInfo(id, name, time, false))

            val addedTask = repository.getTaskByTime(time)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val openIntent = Intent(context, AppOpenReceiver::class.java)
            openIntent.putExtra("packageName", name)
            openIntent.putExtra("id", addedTask.id.toString())
            openIntent.putExtra("time", time.toString())
            val pendingOpenIntent = PendingIntent.getBroadcast(
                context,
                time.toInt(),
                openIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingOpenIntent
            )
        } catch (e: java.lang.Exception) {
            Timber.e(e)
        }
        _success.value = true
    }
}
