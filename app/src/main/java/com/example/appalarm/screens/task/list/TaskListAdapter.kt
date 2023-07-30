package com.example.appalarm.screens.task.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.appalarm.databinding.TaskItemBinding
import com.example.appalarm.models.TaskInfo
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class TaskListAdapter(
    private val onClick: (TaskInfo) -> Unit
) :
    androidx.recyclerview.widget.ListAdapter<TaskInfo, TaskListAdapter.TaskViewHolder>(
        DIFF_CALLBACK
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.from(parent, onClick)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    class TaskViewHolder private constructor(
        private val binding: TaskItemBinding,
        private val onClick: (TaskInfo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TaskInfo) {
            val pattern: DateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a")
            val date = Date(item.startTime)
            val result = pattern.format(date)

            binding.packageName.text = item.packageName
            if (item.isCompleted) {
                binding.statusName.text = "Completed"
            } else {
                binding.statusName.text = "Pending"
            }

            binding.root.setOnClickListener {
                onClick(item)
            }
            binding.scheduleTime.text = "$result"
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onClick: (TaskInfo) -> Unit
            ): TaskViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TaskItemBinding.inflate(layoutInflater, parent, false)
                return TaskViewHolder(binding, onClick)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TaskInfo>() {
            override fun areItemsTheSame(oldItem: TaskInfo, newItem: TaskInfo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: TaskInfo, newItem: TaskInfo): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
