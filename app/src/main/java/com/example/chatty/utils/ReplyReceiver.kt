package com.example.chatty.utils

import android.Manifest
import android.app.Notification
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import com.example.chatty.R
import com.example.chatty.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReplyReceiver: BroadcastReceiver() {
    @Inject
    lateinit var chatRepository: ChatRepository

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReplyReceiver", "Intent received")
        val reply = RemoteInput.getResultsFromIntent(intent)
        val message = reply?.getCharSequence("quick_reply")
        Log.d("ReplyReceiver", "Message: $message")

        val receivedMessage = intent.getStringExtra("last_message")
        val contactName = intent.getStringExtra("contact_name")
        val chatId = intent.getIntExtra("chat_id", 0)

        // sending quick response
        CoroutineScope(Dispatchers.IO).launch {
            chatRepository.sendMessage(message.toString(), chatId.toLong())
        }

        // Build a new notification, which informs the user that the system
        // handled their interaction with the previous notification.
        val repliedNotification = Notification.Builder(context, "new_message")
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle(contactName)
            .setContentText("You: $message")
            .setStyle(Notification.BigTextStyle()
                    .bigText("${contactName}: ${receivedMessage}\nYou: $message")
            )
            .build()
        // Check for notification permission
        with (NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(ReplyReceiver::class.java.toString(), "Permission not granted")
                return@with
            }
        }

        NotificationManagerCompat.from(context).notify(chatId, repliedNotification)

        /*
        CoroutineScope(Dispatchers.IO).launch {
            if (message != null) {
                val chatId = intent.getIntExtra("chat_id", 0)
                val newMessage = Message(
                    content = message.toString(),
                    chatId = chatId.toLong(),
                    timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                    isIncoming = false
                )
                val notifications = Notifications(context)

                chatRepository.sendMessage(newMessage, Notifications(context))
            }
        }*/
    }
}
