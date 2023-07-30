package com.example.appalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.appalarm.db.AppDatabase
import com.example.appalarm.models.TaskInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AppOpenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val value = intent?.getStringExtra("packageName")
        val time = intent?.getStringExtra("time")?.toLong()
        val id = intent?.getStringExtra("id")?.toInt()

        val launchIntent = context?.packageManager?.getLaunchIntentForPackage(
            value!!
        )
        launchIntent?.let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(it)
        }

        val db = AppDatabase(context!!.applicationContext)

        GlobalScope.launch {
            db.taskDao().update(TaskInfo(id!!, value!!, time!!, true))
        }
    }
}
