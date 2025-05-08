package com.example.chatty.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.chatty.MainActivity
import com.example.chatty.R
import com.example.chatty.models.Message

class Notifications(private val context: Context) {
    private val appContext = context.applicationContext
    private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_NEW_MESSAGE = "new_message"
    }

    fun setupChannel() {
        // Create the NotificationChannel.
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_NEW_MESSAGE,
                "New Message",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "New message has arrived"
            }
        )
    }

    fun showNotification(
        message: Message
    ) {
        var builder = NotificationCompat.Builder(appContext, CHANNEL_NEW_MESSAGE)
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle("New Message")
            .setContentText(message.content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(
                PendingIntent.getActivity(
                    appContext,
                    1,
                    Intent(appContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        notificationManager.notify(1234, builder.build())
    }
}