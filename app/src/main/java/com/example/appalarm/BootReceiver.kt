package com.example.appalarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.appalarm.db.AppDatabase
import com.example.appalarm.models.TaskInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            val db = AppDatabase(context!!)
            var notCompletedTask = emptyList<TaskInfo>()
            GlobalScope.launch {
                notCompletedTask = db.taskDao().getUncompletedTask(status = false)
            }

            for (task in notCompletedTask) {
                val time = if (task.startTime >= System.currentTimeMillis()) {
                    task.startTime
                } else {
                    task.startTime + 86400000
                }
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val openIntent = Intent(context, AppOpenReceiver::class.java)
                openIntent.putExtra("packageName", task.packageName)
                openIntent.putExtra("id", task.id.toString())
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
            }
        }
    }
}
