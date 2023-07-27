package com.example.appalarm.screens.task.list

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.appalarm.AppOpenReceiver
import com.example.appalarm.R
import com.example.appalarm.databinding.FragmentTaskListScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListScreenFragment : Fragment(R.layout.fragment_task_list_screen) {

    private val viewModel: TaskListScreenViewModel by viewModels()
    private lateinit var binding: FragmentTaskListScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTaskListScreenBinding.bind(view)
        binding.addFab.setOnClickListener {
            findNavController().navigate(
                TaskListScreenFragmentDirections
                    .actionTaskListScreenFragmentToTaskAddScreenFragment()
            )
        }
        loadData()
        checkTask()
    }

    private fun initObserver() {
        viewModel.currentTask.observe(this) {
            if (it != null) {
                val alarmManager =
                    requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val openIntent = Intent(context, AppOpenReceiver::class.java)
                openIntent.putExtra("packageName", it.packageName)
                val pendingOpenIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    openIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, it.startTime, pendingOpenIntent)
            }
        }
    }

    private fun loadData() {
    }

    private fun checkTask() {
        val time = System.currentTimeMillis()
        viewModel.checkTask(time)
    }
}
