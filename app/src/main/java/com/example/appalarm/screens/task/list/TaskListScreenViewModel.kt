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
    private val _task: MutableLiveData<List<TaskInfo>> by lazy {
        MutableLiveData<List<TaskInfo>>()
    }

    val task: LiveData<List<TaskInfo>?>
        get() = _task

    private val _deleteSuccess: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val deleteSuccess: LiveData<Boolean>
        get() = _deleteSuccess

    fun getTask() = viewModelScope.launch {
        try {
            val response = repository.getTask()
            _task.value = response
        } catch (e: Exception) {
            Timber.tag("Found").d(e)
        }
    }

    fun deleteTask(
        task: TaskInfo
    ) = viewModelScope.launch {
        try {
            repository.deleteTask(task)
            _deleteSuccess.postValue(true)
        } catch (e: Exception) {
            Timber.tag("error").d(e)
        }
    }
}
