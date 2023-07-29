package com.example.appalarm.screens.task.add

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
import java.lang.Exception
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class TaskAddScreenViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _success: MutableLiveData<Boolean?> by lazy {
        MutableLiveData<Boolean?>(false)
    }

    val success: LiveData<Boolean?>
        get() = _success

    private val _message: MutableLiveData<String?> by lazy {
        MutableLiveData<String?>(null)
    }

    val message: LiveData<String?>
        get() = _message

    private fun isValid(
        name: String
    ): Boolean {
        if (name.isBlank()) {
            return false
        }

        return true
    }

    @SuppressLint("ScheduleExactAlarm")
    fun insertTask(
        context: Context,
        name: String,
        time: Long
    ) = viewModelScope.launch {
        val response = repository.checkSameTime(time)
        if (!isValid(name) && response.isEmpty()) {
            return@launch
        }
        if (!response.isEmpty()) {
            _message.value = "Please set different time"
            return@launch
        }
        try {
            repository.addTask(TaskInfo(0, name, time))
            _success.value = true

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
        } catch (e: Exception) {
            Timber.tag("error_message").d(e)
        }
    }
}
