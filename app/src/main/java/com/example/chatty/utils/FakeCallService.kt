package com.example.chatty.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import com.example.chatty.R
import com.example.chatty.models.Chat
import android.util.Log

class FakeCallService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("FakeCallService", "Service started")

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

        // Decline PendingIntent (BroadcastReceiver)
        val declineIntent = Intent(this, CallReceiver::class.java).apply {
            action = "DECLINE_CALL"
        }
        val declinePendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            declineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

// Answer PendingIntent (Activity)
        val answerIntent = Intent(this, OngoingCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val answerPendingIntent = PendingIntent.getActivity(
            this,
            1,
            answerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(appContext, CHANNEL_CALL)
            .setSmallIcon(R.drawable.ic_message)
            .setStyle(
                NotificationCompat.CallStyle.forIncomingCall(
                    person,
                    declinePendingIntent,
                    answerPendingIntent
                )
            )
            .setChannelId(CHANNEL_CALL)

        return builder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("FakeCallService", "Service destroyed")
    }

}