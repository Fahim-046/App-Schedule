package com.example.appalarm.screens.task.add

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.appalarm.R
import com.example.appalarm.databinding.FragmentTaskAddScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class TaskAddScreenFragment : Fragment(R.layout.fragment_task_add_screen) {

    private lateinit var currentTime: Calendar

    private lateinit var binding: FragmentTaskAddScreenBinding

    private val viewModel: TaskAddScreenViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTaskAddScreenBinding.bind(view)

        initViews()
    }

    private fun initObserver() {
        viewModel.success.observe(this) {
            if (it == true) {
                Toast.makeText(requireContext(), "Task added in the schedule", Toast.LENGTH_SHORT)
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

    private fun initViews() {
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
                },
                startHour,
                startMinute,
                false
            ).show()
        }

        binding.addToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // packages
        val allPackages = getAllPackages()
        val packageAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner_item,
            allPackages
        )
        binding.tvPackage.setAdapter(packageAdapter)

        binding.saveBtn.setOnClickListener {
            viewModel.insertTask(
                requireContext(),
                binding.tvPackage.text.toString(),
                currentTime.timeInMillis
            )
        }
    }

    private fun getAllPackages(): List<String> {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val packages = requireContext().packageManager.queryIntentActivities(mainIntent, 0)
        lateinit var name: String
        val installedApps = mutableListOf<String>()
        for (data in packages) {
            name = data.activityInfo.packageName
            installedApps.add(name)
        }
        return installedApps
    }
}
