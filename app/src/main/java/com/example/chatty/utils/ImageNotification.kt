package com.example.chatty.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.chatty.R
import com.example.chatty.models.Chat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



class ImageNotification (private val context: Context) {
    private val appContext = context.applicationContext
    private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_IMAGE = "Profile_Image"
    }

    fun setupChannel() {
        // Create the NotificationChannel.
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_IMAGE,
                "Profile Image",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Shows the profile image of the contact"
            }
        )
    }

    fun showNotification(
        chat : Chat
    ) {
        // Notification action: open chat (message fragment) of the provided contact (senderId)
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            1,
            Intent(Intent.ACTION_VIEW, "chatty://chat/${chat.id}".toUri()).apply {
                setPackage(context.packageName)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val builder = NotificationCompat.Builder(appContext, CHANNEL_IMAGE)
            .setSmallIcon(R.drawable.ic_message)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, chat.icon))   //this is needed for the big picture style
            .setContentTitle(chat.name)
            .setContentIntent(pendingIntent) // notification action
            .setAutoCancel(true) // destroy notification when clicked
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(BitmapFactory.decodeResource(context.resources, chat.icon))
                .bigLargeIcon(null as Bitmap?)
            )

        // i change the notification id based on the chat that sent the notification
        notificationManager.notify(chat.id.toInt()+5, builder.build())
    }

    fun showFakeDownloadAndImageNotification(chat: Chat) {
        val notificationId = chat.id.toInt() + 5

        // Show progress notification
        val progressBuilder = NotificationCompat.Builder(appContext, CHANNEL_IMAGE)
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle("Downloading image...")
            .setContentText("Please wait")
            .setProgress(100, 0, false)
            .setOngoing(true)

        notificationManager.notify(notificationId, progressBuilder.build())

        // Simulate download with coroutine
        CoroutineScope(Dispatchers.Main).launch {
            for (progress in 0..100 step 20) {
                delay(400)
                progressBuilder.setProgress(100, progress, false)
                notificationManager.notify(notificationId, progressBuilder.build())
            }
            // Remove progress bar and show image notification
            notificationManager.cancel(notificationId)
            showNotification(chat)
        }
    }
}