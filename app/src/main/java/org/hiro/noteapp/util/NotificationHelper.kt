package org.hiro.noteapp.util

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Message
import androidx.core.app.NotificationCompat


class NotificationHelper(base: Context) : ContextWrapper(base) {
    private var mManager: NotificationManager? = null
    
    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel =
            NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(channel)
    }
    
    val manager: NotificationManager
        get() {
            if (mManager == null) {
                mManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return mManager!!
        }
    
    fun getChannelNotification(message: String?): NotificationCompat.Builder =
        NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle("Task reminder")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_edit)
    
    companion object {
        const val channelID = "channelID"
        const val channelName = "Channel Name"
    }
    
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }
}