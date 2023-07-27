package com.example.appalarm.screens.task.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private fun isValid(
        name: String
    ): Boolean {
        if (name.isBlank()) {
            return false
        }

        return true
    }

    fun insertTask(
        name: String,
        time: String
    ) = viewModelScope.launch {
        val timeInMillis = time.toLong()
        val response = repository.checkSameTime(timeInMillis)
        if (!isValid(name) && response.isEmpty()) {
            return@launch
        }
        try {
            repository.addTask(TaskInfo(0, name, timeInMillis))
            _success.value = true
        } catch (e: Exception) {
            Timber.tag("error_message").d(e.message)
        }
    }
}
