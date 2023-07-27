package com.example.appalarm.screens.task.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appalarm.models.TaskInfo
import com.example.appalarm.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class TaskListScreenViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    private val _currentTask: MutableLiveData<TaskInfo?> by lazy {
        MutableLiveData<TaskInfo?>()
    }

    val currentTask: LiveData<TaskInfo?>
        get() = _currentTask

    fun checkTask(
        time: Long
    ) = viewModelScope.launch {
        try {
            val response = repository.checkTask(time)

            if (response != null) {
                _currentTask.value = response
            }
        } catch (e: Exception) {
            Timber.tag("Found").d(e)
        }
    }
}
