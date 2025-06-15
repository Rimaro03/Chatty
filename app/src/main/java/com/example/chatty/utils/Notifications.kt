package com.example.chatty.utils

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
import androidx.media3.session.MediaSession
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.chatty.R
import com.example.chatty.models.Message
import androidx.core.net.toUri
import com.example.chatty.models.Chat
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaStyleNotificationHelper
import com.example.chatty.BubbleActivity
import com.example.chatty.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Notifications @Inject constructor(
    private val application: Application
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
        private const val CHANNEL_BUBBLE = "bubble"

        private val lastTwoMessages = mutableListOf<Pair<String, String>>("contact_name" to "message_content", "contact_name" to "message_content")
    }

    // create the RemoteInput used to implement the quick reply feature
    var remoteInput : RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
        setLabel(application.getString(R.string.reply_label))
        build()
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
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_MEDIA,
                "New Message",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Media Playing"
            }
        )
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_BUBBLE,
                "New Message",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                setAllowBubbles(true)
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
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE )

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
                putExtra("chat_id", message.chatId.toInt())
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // BUBBLE
        val target = Intent(appContext, BubbleActivity::class.java).apply {
            setPackage(application.packageName)
            putExtra("contactId", message.chatId)
            action = Intent.ACTION_VIEW
        }

        val bubbleIntent = PendingIntent.getActivity(
            appContext, chat.id.toInt(), target, PendingIntent.FLAG_MUTABLE
        )

        val chatPartner = Person.Builder()
            .setName(chat.name)
            .setImportant(true)
            .build()

        val shortcutID = "Shortcut ${chat.name}"
        val shortcut = ShortcutInfo.Builder(appContext, shortcutID)
            .setIntent(target)
            .setShortLabel(chatPartner.name!!)
            .setLongLived(true)
            .build()
        appContext.getSystemService(ShortcutManager::class.java)?.pushDynamicShortcut(shortcut)

        val bubbleMetadata = NotificationCompat.BubbleMetadata.Builder(
            bubbleIntent,
            IconCompat.createWithResource(appContext, chat.icon)
        ).setDesiredHeight(600)
            .setAutoExpandBubble(true)
            .setSuppressNotification(true)
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
            .addAction(NotificationCompat.Action.Builder(
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
        notificationManager.notify(message.chatId.toInt(), builder.build())

        // create a summary notification
        if (lastTwoMessages[1].first != "contact_name" && lastTwoMessages[0].first != "contact_name") {
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

    @OptIn(UnstableApi::class)
    fun createMediaNotification(isPlaying: Boolean, mediaSession: MediaSession) {
        val playPauseAction = if (isPlaying) {
            val intent = Intent(application, PlaybackManager::class.java).apply {
                this.action = "pause"
            }

            NotificationCompat.Action(
                android.R.drawable.ic_media_pause, "Pause",
                PendingIntent.getService(application, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            )
        } else {
            val intent = Intent(application, PlaybackManager::class.java).apply {
                this.action = "play"
            }

            NotificationCompat.Action(
                android.R.drawable.ic_media_play, "Play",
                PendingIntent.getService(application, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            )
        }

        val stopIntent = Intent(application, PlaybackManager::class.java).apply {
            this.action = "stop"
        }

        val stopAction = NotificationCompat.Action(
            android.R.drawable.ic_menu_close_clear_cancel, "Stop",
            PendingIntent.getService(application, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)
        )

        val mainActivityIntent = Intent(application, MainActivity::class.java)

        val notification = NotificationCompat.Builder(application, CHANNEL_MEDIA)
            // Show controls on lock screen even when user hides sensitive content.
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.music_note_icon)
            // Add media control buttons that invoke intents in your media service
            .addAction(playPauseAction)
            .addAction(stopAction)
            .setContentIntent(PendingIntent.getActivity(application, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE))
            // Apply the media style template.
            .setStyle(MediaStyleNotificationHelper.MediaStyle(mediaSession)
                .setShowActionsInCompactView(1 /* #1: pause button \*/))
            .setContentTitle("Now Playing")
            .setContentText("Media Audio")

        notificationManager.notify(0, notification.build())
    }
}