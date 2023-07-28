package com.example.appalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppOpenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val value = intent?.getStringExtra("packageName")
        val launchIntent = context!!.packageManager.getLaunchIntentForPackage(
            value!!
        ) // Replace with your app's package name
        launchIntent?.let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(it)
        }
    }
}
