package com.example.chatty.notifications

import android.app.Application
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
import javax.inject.Inject

class ImageNotification @Inject constructor(
    private val application: Application
) {
    private val appContext = application.applicationContext
    private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_IMAGE = "Profile_Image"
    }

    fun showNotification(
        chat : Chat
    ) {
        // Notification action: open chat (message fragment) of the provided contact (senderId)
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            1,
            Intent(Intent.ACTION_VIEW, "chatty://chat/${chat.id}".toUri()).apply {
                setPackage(application.packageName)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val builder = NotificationCompat.Builder(appContext, CHANNEL_IMAGE)
            .setSmallIcon(R.drawable.ic_message)
            .setLargeIcon(BitmapFactory.decodeResource(appContext.resources, chat.icon))   //this is needed for the big picture style
            .setContentTitle(chat.name)
            .setContentIntent(pendingIntent) // notification action
            .setAutoCancel(true) // destroy notification when clicked
            .setStyle(
                NotificationCompat.BigPictureStyle()
                .bigPicture(BitmapFactory.decodeResource(appContext.resources, chat.icon))
                .bigLargeIcon(null as Bitmap?)
            )

        // i change the notification id based on the chat that sent the notification
        notificationManager.notify(3, builder.build())
    }

    fun showFakeDownloadAndImageNotification(chat: Chat) {
        // Show progress notification
        val progressBuilder = NotificationCompat.Builder(appContext, CHANNEL_IMAGE)
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle("Downloading image...")
            .setContentText("Please wait")
            .setProgress(100, 0, false)
            .setOngoing(true)

        notificationManager.notify(2, progressBuilder.build())

        // Simulate download with coroutine
        CoroutineScope(Dispatchers.Main).launch {
            for (progress in 0..100 step 20) {
                delay(400)
                progressBuilder.setProgress(100, progress, false)
                notificationManager.notify(2, progressBuilder.build())
            }
            // Remove progress bar and show image notification
            notificationManager.cancel(2)
            showNotification(chat)
        }
    }
}