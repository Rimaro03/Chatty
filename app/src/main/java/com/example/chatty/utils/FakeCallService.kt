package com.example.chatty.com.example.chatty.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.net.toUri
import com.example.chatty.R
import com.example.chatty.models.Chat

class FakeCallService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // take the chat from the intent
        val chatId = intent?.getLongExtra("chatId", 0L)
        val chatName = intent?.getStringExtra("chatName") ?: "Caller Name"
        val chatIcon = intent?.getIntExtra("chatIcon", R.drawable.boneca)

        val chat = Chat(
            id = chatId!!,
            name = chatName,
            icon = chatIcon!!,
        )

        setupChannel()
        // i set the id to 1 + chat.id to avoid conflict with the notification id
        startForeground(chat.id.toInt() + 1, createNotification(chat))
        return START_NOT_STICKY
    }

    private val appContext by lazy {
        applicationContext
    }
    private val notificationManager by lazy {
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        private const val CHANNEL_CALL = "call_notification"
    }

    fun setupChannel() {
        // Create the NotificationChannel.
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_CALL,
                "Incoming Call",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Incoming call notification"
            }
        )
    }

    fun createNotification(
        chat: Chat
    ) : android.app.Notification {
        // person for the call
        val person = Person.Builder()
            .setName(chat.name)
            .setIcon(androidx.core.graphics.drawable.IconCompat.createWithResource(appContext, chat.icon))
            .build()

         // Notification action: open chat (message fragment) of the provided contact (senderId)
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            1,
            Intent(Intent.ACTION_VIEW, "chatty://chat/${chat.id}".toUri()).apply {
                setPackage(appContext.packageName)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val builder = NotificationCompat.Builder(appContext, CHANNEL_CALL)
            .setSmallIcon(R.drawable.ic_message)
            .setStyle(
                NotificationCompat.CallStyle.forIncomingCall(
                    person,
                    pendingIntent,
                    pendingIntent
                )
            )
            .setChannelId(CHANNEL_CALL)

        return builder.build()
    }

}