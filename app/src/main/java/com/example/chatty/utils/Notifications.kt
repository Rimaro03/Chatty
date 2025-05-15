package com.example.chatty.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.chatty.R
import com.example.chatty.models.Message
import androidx.core.net.toUri
import com.example.chatty.models.Chat

class Notifications(private val context: Context) {
    private val appContext = context.applicationContext
    private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // create the RemoteInput used to implement the quick reply feature
    private val KEY_TEXT_REPLY = "quick_reply"
    var replyLabel: String = context.getString(R.string.reply_label)
    var remoteInput : RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
        setLabel(replyLabel)
        build()
    }

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
        message: Message,
        chat: Chat
    ) {
        // Notification action: open chat (message fragment) of the provided contact (senderId)
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            1,
            Intent(Intent.ACTION_VIEW, "chatty://chat/${message.chatId}".toUri()).apply {
                setPackage(context.packageName)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val markAsReadIntent = PendingIntent.getBroadcast(
            appContext,
            2,
            Intent(context, ReadReceiver::class.java).apply {
                putExtra("chat_id", message.chatId.toInt())
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val replyPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            3,
            Intent(context, ReplyReceiver::class.java).apply {
                putExtra("last_message", message.content)
                putExtra("contact_name", chat.name)
                putExtra("chat_id", message.chatId.toInt())
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val builder = NotificationCompat.Builder(appContext, CHANNEL_NEW_MESSAGE)
            .setSmallIcon(R.drawable.ic_message)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, chat.icon))   //this is needed for the big picture style
            .setContentTitle(chat.name)
            .setContentText(message.content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // notification action
            .setAutoCancel(true) // destroy notification when clicked
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message.content))  // the notification is now expandable
            .addAction(NotificationCompat.Action.Builder(
                R.drawable.ic_message,
                "Reply",
                replyPendingIntent
            ).addRemoteInput(remoteInput).build())  // add the reply action
            .addAction(R.drawable.boneca, "Mark as Read", markAsReadIntent) // mark the message as read
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // i change the notification id based on the chat that sent the notification
        notificationManager.notify(message.chatId.toInt(), builder.build())
    }
}