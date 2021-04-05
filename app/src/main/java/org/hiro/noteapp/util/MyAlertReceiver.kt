package org.hiro.noteapp.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MyAlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val desc = intent?.extras?.getString("TASK_DESC")
        val notificationHelper = NotificationHelper(context!!)
        val nb: NotificationCompat.Builder = notificationHelper.getChannelNotification(desc)
        Toast.makeText(context, desc, Toast.LENGTH_SHORT).show()
        notificationHelper.manager.notify(1, nb.build())
    }
}