package com.example.appalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppOpenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
//        val db = AppDatabase(context!!)
        val value = intent?.getStringExtra("packageName")
        val time = intent?.getStringExtra("time")?.toLong()
        val id = intent?.getStringExtra("id")?.toInt()
//        GlobalScope.launch {
//            db.taskDao().update(TaskInfo(id!!, value!!, time!!, true))
//        }
        val launchIntent = context?.packageManager?.getLaunchIntentForPackage(
            value!!
        )
        launchIntent?.let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(it)
        }
    }
}
