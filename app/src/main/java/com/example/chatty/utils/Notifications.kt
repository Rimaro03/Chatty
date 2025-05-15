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
        private const val GROUP_NOTIFICATION = "group_notification"
        val lastTwoMessages = mutableListOf<Pair<String, String>>("contact_name" to "message_content", "contact_name" to "message_content")
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
        if (lastTwoMessages[0].first == chat.name) {
            lastTwoMessages[0] = chat.name to message.content
        } else if (lastTwoMessages[1].first == chat.name) {
            lastTwoMessages[1] = lastTwoMessages[0]
            lastTwoMessages[0] = chat.name to message.content
        } else {
            lastTwoMessages[1] = lastTwoMessages[0]
            lastTwoMessages[0] = chat.name to message.content
        }

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
            .setGroup(GROUP_NOTIFICATION)

        // i change the notification id based on the chat that sent the notification
        notificationManager.notify(message.chatId.toInt(), builder.build())

        // create a summary notification
        if (lastTwoMessages.size > 1) {
            createSummeryNotification()
        }
    }

    private fun createSummeryNotification() {
        val summaryBuilder = NotificationCompat.Builder(appContext, CHANNEL_NEW_MESSAGE)
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle("New messages")
            .setStyle(NotificationCompat.InboxStyle()
                .addLine("${lastTwoMessages[0].first}: ${lastTwoMessages[0].second}")
                .addLine("${lastTwoMessages[1].first}: ${lastTwoMessages[1].second}")
                .setSummaryText("+${lastTwoMessages.size - 2} more"))
            .setGroup(GROUP_NOTIFICATION)
            .setGroupSummary(true)

        notificationManager.notify(0, summaryBuilder.build())
    }
}