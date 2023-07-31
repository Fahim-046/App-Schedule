package com.example.appalarm.screens.task.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appalarm.R
import com.example.appalarm.databinding.FragmentTaskListScreenBinding
import com.example.appalarm.models.TaskInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListScreenFragment : Fragment(R.layout.fragment_task_list_screen) {
    private val viewModel: TaskListScreenViewModel by viewModels()

    private lateinit var binding: FragmentTaskListScreenBinding

    private lateinit var adapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObserver()

        adapter = TaskListAdapter { task ->
            adapterOnClick(task)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTaskListScreenBinding.bind(view)
        loadData()
        initViews()
        initListeners()
    }

    private fun initObserver() {
        viewModel.task.observe(this) { task ->
            if (task != null) {
                adapter.submitList(task)
            }
        }

        viewModel.deleteSuccess.observe(this) {
            if (it == true) {
                viewModel.getTask()
            }
        }
    }

    private fun initListeners() {
        binding.addFab.setOnClickListener {
            findNavController().navigate(
                TaskListScreenFragmentDirections
                    .actionTaskListScreenFragmentToTaskAddScreenFragment()
            )
        }
    }

    private fun loadData() {
        viewModel.getTask()
    }

    private fun initViews() {
        binding.taskList.layoutManager = LinearLayoutManager(activity)
        binding.taskList.adapter = adapter
    }

    private fun adapterOnClick(task: TaskInfo) {
        val action =
            TaskListScreenFragmentDirections.actionTaskListScreenFragmentToTaskUpdateFragment(
                task.id
            )
        findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTask()
    }
}
