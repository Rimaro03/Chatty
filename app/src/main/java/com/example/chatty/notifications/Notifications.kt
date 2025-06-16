package com.example.chatty.notifications

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import android.os.VibrationEffect
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.example.chatty.BubbleActivity
import com.example.chatty.R
import com.example.chatty.models.Chat
import com.example.chatty.models.Message
import com.example.chatty.utils.ReadReceiver
import com.example.chatty.utils.ReplyReceiver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Notifications @Inject constructor(
    private val application: Application,
) {
    private val appContext = application.applicationContext
    private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        Log.d("Notifications", "Notification created")
    }

    companion object {
        private const val KEY_TEXT_REPLY = "quick_reply"
        private const val CHANNEL_NEW_MESSAGE = "new_message"
        private const val GROUP_NOTIFICATION = "group_notification"
        private const val CHANNEL_MEDIA = "media"
        private const val CHANNEL_IMAGE = "Profile_Image"
        private const val CHANNEL_CALL = "call_notification"

        private val lastTwoMessages = mutableListOf<Pair<String, String>>("contact_name" to "message_content", "contact_name" to "message_content")
    }

    // create the RemoteInput used to implement the quick reply feature
    var remoteInput : RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
        setLabel(application.getString(R.string.reply_label))
        build()
    }

    fun setupChannel() {
        // message channel
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_NEW_MESSAGE,
                "New Message",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "New message has arrived"
                setAllowBubbles(true)
            }
        )

        // media channel
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_MEDIA,
                "Playback manager",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Media Playing"
            }
        )

        // call channel
        val timings = longArrayOf(0, 100, 50, 100, 50, 100) // Custom vibration pattern
        val amplitudes = intArrayOf(255, 0, 255, 0, 255, 0) // Custom amplitude pattern
        val vibration : VibrationEffect = VibrationEffect.createWaveform(timings, amplitudes, 3)
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_CALL,
                "Incoming Call",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Incoming call notification"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM){
                    setVibrationEffect(vibration)
                }
            }

        )

        // download channel
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
        message: Message,
        chat: Chat
    ) {
        if (lastTwoMessages[0].first == chat.name) {
            // I update the message with the current new message
            lastTwoMessages[0] = chat.name to message.content
            Log.d("Notifications", "Name[0]: ${lastTwoMessages[0].first}, Message[0]: ${lastTwoMessages[0].second}")
            Log.d("Notifications", "Name[1]: ${lastTwoMessages[1].first}, Message[1]: ${lastTwoMessages[1].second}")
        } else {
            lastTwoMessages[1] = lastTwoMessages[0]
            lastTwoMessages[0] = chat.name to message.content
            Log.d("Notifications", "Name[0]: ${lastTwoMessages[0].first}, Message[0]: ${lastTwoMessages[0].second}")
            Log.d("Notifications", "Name[1]: ${lastTwoMessages[1].first}, Message[1]: ${lastTwoMessages[1].second}")
        }

        // Notification action: open chat (message fragment) of the provided contact (senderId)
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            1,
            Intent(Intent.ACTION_VIEW, "chatty://chat/${message.chatId}".toUri()).apply {
                setPackage(application.packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val markAsReadIntent = PendingIntent.getBroadcast(
            appContext,
            2 + chat.id.toInt(),
            Intent(application, ReadReceiver::class.java).apply {
                putExtra("chat_id", message.chatId.toInt())
                putExtra("message_id", message.id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val replyPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            application,
            3 + chat.id.toInt(),
            Intent(application, ReplyReceiver::class.java).apply {
                putExtra("last_message", message.content)
                putExtra("contact_name", chat.name)
                putExtra("contact_icon", chat.icon)
                putExtra("chat_id", message.chatId.toInt())
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // BUBBLE
        val target = Intent(appContext, BubbleActivity::class.java).apply {
            setPackage(application.packageName)
            putExtra("contactId", message.chatId)
            action= Intent.ACTION_VIEW
        }

        val bubbleIntent = PendingIntent.getActivity(
            appContext, chat.id.toInt(), target, PendingIntent.FLAG_MUTABLE
        )

        val chatPartner = Person.Builder()
            .setName(chat.name)
            .setIcon(IconCompat.createWithResource(appContext, chat.icon))
            .setImportant(true)
            .build()

        val shortcutID = "chat_${chat.id}"
        val shortcut = ShortcutInfoCompat.Builder(appContext, shortcutID)
            .setIntent(target)
            .setShortLabel(chatPartner.name!!)
            .setLongLived(true)
            .setPerson(chatPartner)
            .setIcon(IconCompat.createWithResource(appContext, chat.icon))
            .build()
        ShortcutManagerCompat.pushDynamicShortcut(appContext, shortcut)

        val bubbleMetadata = NotificationCompat.BubbleMetadata.Builder(
            bubbleIntent,
            IconCompat.createWithResource(appContext, chat.icon)
        ).setDesiredHeight(600)
            .setAutoExpandBubble(true)
            .setSuppressNotification(true)
            .setIcon(IconCompat.createWithResource(appContext, chat.icon))
            .build()

        val builder = NotificationCompat.Builder(appContext, CHANNEL_NEW_MESSAGE)
            .setSmallIcon(R.drawable.ic_message)
            .setLargeIcon(BitmapFactory.decodeResource(application.resources, chat.icon))   //this is needed for the big picture style
            .setContentTitle(chat.name)
            .setContentText(message.content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // notification action
            .setAutoCancel(true) // destroy notification when clicked
            .setStyle(
                NotificationCompat.MessagingStyle(chatPartner)
                    .addMessage(message.content, System.currentTimeMillis(), chatPartner)
            )
            .addAction(
                NotificationCompat.Action.Builder(
                R.drawable.ic_message,
                "Reply",
                replyPendingIntent
            )
                .addRemoteInput(remoteInput).build())  // add the reply action
            .addAction(
                R.drawable.ic_message,
                "Mark as Read",
                markAsReadIntent
            ) // mark the message as read
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setGroup(GROUP_NOTIFICATION)
            .setBubbleMetadata(bubbleMetadata)
            .setShortcutId(shortcutID)
            .addPerson(chatPartner)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)

        // i change the notification id based on the chat that sent the notification
        notificationManager.notify(message.chatId.toInt()+10, builder.build())

        // create a summary notification
        if (lastTwoMessages[1].first != "contact_name" && lastTwoMessages[0].first != "contact_name") {
            createSummeryNotification()
        }
    }

    private fun createSummeryNotification() {
        val summaryBuilder = NotificationCompat.Builder(appContext, CHANNEL_NEW_MESSAGE)
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle("New messages")
            .setStyle(
                NotificationCompat.InboxStyle()
                .addLine("${lastTwoMessages[0].first}: ${lastTwoMessages[0].second}")
                .addLine("${lastTwoMessages[1].first}: ${lastTwoMessages[1].second}")
                .setSummaryText("+${lastTwoMessages.size - 2} more"))
            .setGroup(GROUP_NOTIFICATION)
            .setGroupSummary(true)

        notificationManager.notify(0, summaryBuilder.build())
    }
}