package com.example.chatty.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import com.example.chatty.R
import android.util.Log

class FakeCallService : Service() {

    // Person that represents the caller
    private lateinit var person: Person
    // id of the chat
    private var id : Long = 0L

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // take the chat from the intent
        id = intent?.getLongExtra("chatId", 0L)!!

        person = Person.Builder()
            .setName(intent.getStringExtra("chatName") ?: "Caller Name")
            .setIcon(androidx.core.graphics.drawable.IconCompat.createWithResource(appContext, intent.getIntExtra("chatIcon", R.drawable.boneca)))
            .build()

        when (intent.action) {
            "INCOMING_CALL" -> {
                // i set the id to 1 + chat.id to avoid conflict with the notification id
                startForeground(4, createIncomingNotification())
            }
            "ONGOING_CALL" -> {
                // i set the id to 1 + chat.id to avoid conflict with the notification id
                startForeground(4, createOngoingNotification())
            }
        }

        return START_NOT_STICKY
    }

    private val appContext by lazy {
        applicationContext
    }
    private val notificationManager by lazy {
        appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        private const val CHANNEL_CALL = "call_notification"
    }

    fun createIncomingNotification() : android.app.Notification {
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

        // Answer PendingIntent (BroadcastReceiver)
        val answerIntent = Intent(this, CallReceiver::class.java).apply {
            action = "ANSWER_CALL"
            putExtra("chatId", id)
            putExtra("chatName", person.name)
            putExtra("chatIcon", person.icon?.resId)
        }
        val answerPendingIntent = PendingIntent.getBroadcast(
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

    fun createOngoingNotification () : android.app.Notification {

        val hangUpIntent = Intent(this, CallReceiver::class.java).apply {
            action = "DECLINE_CALL"
        }
        val hangUpPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            hangUpIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(appContext, CHANNEL_CALL)
            .setSmallIcon(R.drawable.ic_message)
            .setStyle(
                NotificationCompat.CallStyle.forOngoingCall(
                    person,
                    hangUpPendingIntent,
                )
            )
            .setChannelId(CHANNEL_CALL)

        return builder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("FakeCallService", "Service destroyed")
        val intent = Intent("CALL_ENDED")
        sendBroadcast(intent)
    }

}