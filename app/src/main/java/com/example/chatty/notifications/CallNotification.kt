package com.example.chatty.notifications

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import com.example.chatty.R
import com.example.chatty.utils.CallReceiver
import javax.inject.Inject

class CallNotification @Inject constructor(
    application: Application
) {
    companion object {
        private const val CHANNEL_CALL = "call_notification"
    }

    private val appContext = application.applicationContext

    fun createIncomingNotification(chatId: Long, person: Person) : android.app.Notification {
        // Decline PendingIntent (BroadcastReceiver)
        val declineIntent = Intent(appContext, CallReceiver::class.java).apply {
            action = "DECLINE_CALL"
        }
        val declinePendingIntent = PendingIntent.getBroadcast(
            appContext,
            0,
            declineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Answer PendingIntent (BroadcastReceiver)
        val answerIntent = Intent(appContext, CallReceiver::class.java).apply {
            action = "ANSWER_CALL"
            putExtra("chatId", chatId)
            putExtra("chatName", person.name)
            putExtra("chatIcon", person.icon?.resId)
        }
        val answerPendingIntent = PendingIntent.getBroadcast(
            appContext,
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

    fun createOngoingNotification (person: Person) : android.app.Notification {
        val hangUpIntent = Intent(appContext, CallReceiver::class.java).apply {
            action = "DECLINE_CALL"
        }
        val hangUpPendingIntent = PendingIntent.getBroadcast(
            appContext,
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
}