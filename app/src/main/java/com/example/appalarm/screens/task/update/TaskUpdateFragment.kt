package com.example.appalarm.screens.task.update

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.appalarm.AppOpenReceiver
import com.example.appalarm.R
import com.example.appalarm.databinding.FragmentTaskUpdateBinding
import com.example.appalarm.models.TaskInfo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class TaskUpdateFragment : Fragment(R.layout.fragment_task_update) {

    private val args: TaskUpdateFragmentArgs by navArgs()

    private lateinit var binding: FragmentTaskUpdateBinding

    private val viewModel: TaskUpdateScreenViewModel by viewModels()

    private var packageName: String = ""
    private var startTime = 0L
    private var id = 0
    private var status = false

    private lateinit var currentTime: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        viewModel.task.observe(this) {
            if (it != null) {
                binding.tvPackage.text = it.packageName
                packageName = it.packageName
                id = it.id
                startTime = it.startTime
                status = it.isCompleted
            }
        }

        viewModel.deleteSuccess.observe(this) {
            if (it == true) {
                findNavController().popBackStack()
            }
        }

        viewModel.success.observe(this) {
            if (it == true) {
                Toast.makeText(requireContext(), "Task updated in the schedule", Toast.LENGTH_SHORT)
                    .show()
                findNavController().popBackStack()
            }
        }

        viewModel.message.observe(this) {
            if (it != null) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTaskUpdateBinding.bind(view)
        initListeners()
        getTask()
    }

    private fun getTask() {
        viewModel.getTask(args.taskId)
    }

    private fun initListeners() {
        binding.startBtn.setOnClickListener {
            currentTime = Calendar.getInstance()
            val startHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = currentTime.get(Calendar.MINUTE)

            TimePickerDialog(
                requireContext(),
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    binding.etStartTime.setText("$hourOfDay : $minute")
                    currentTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    currentTime.set(Calendar.MINUTE, minute)
                    currentTime.set(Calendar.SECOND, 0)
                },
                startHour,
                startMinute,
                false
            ).show()
        }

        binding.saveBtn.setOnClickListener {
            val alarmManager =
                requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val openIntent = Intent(context, AppOpenReceiver::class.java)
            openIntent.putExtra("packageName", packageName)
            val pendingOpenIntent = PendingIntent.getBroadcast(
                context,
                startTime.toInt(),
                openIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingOpenIntent)
            viewModel.updateTask(requireContext(), id, packageName, currentTime.timeInMillis)
        }

        binding.deleteBtn.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Do you want to delete this schedule?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes") { _, _ ->
                    val alarmManager =
                        requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    val openIntent = Intent(context, AppOpenReceiver::class.java)
                    openIntent.putExtra("packageName", packageName)
                    val pendingOpenIntent = PendingIntent.getBroadcast(
                        context,
                        startTime.toInt(),
                        openIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.cancel(pendingOpenIntent)
                    viewModel.deleteTask(
                        TaskInfo(
                            id,
                            packageName,
                            startTime,
                            status
                        )
                    )
                }
                .show()
        }

        binding.addToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
