package com.example.chatty.utils

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import com.example.chatty.R


@AndroidEntryPoint
class ReplyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReplyReceiver", "Intent received")
        val reply = RemoteInput.getResultsFromIntent(intent)
        val message = reply?.getCharSequence("quick_reply")
        Log.d("ReplyReceiver", "Message: $message")

        // Build a new notification, which informs the user that the system
        // handled their interaction with the previous notification.
        val repliedNotification = Notification.Builder(context, "new_message")
            .setSmallIcon(R.drawable.ic_message)
            .setContentText("You replied: $message")
            .build()

        NotificationManagerCompat.from(context).notify(1234, repliedNotification)
    }
}
