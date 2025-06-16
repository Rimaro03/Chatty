package com.example.chatty.utils

import android.Manifest
import android.app.Application
import android.app.Notification
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import dagger.hilt.android.AndroidEntryPoint
import com.example.chatty.R
import com.example.chatty.models.Chat
import com.example.chatty.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReplyReceiver: BroadcastReceiver() {
    @Inject
    lateinit var chatRepository: ChatRepository

    companion object {
        private const val CHANNEL_NEW_MESSAGE = "new_message"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reply = RemoteInput.getResultsFromIntent(intent)
        val message = reply?.getCharSequence("quick_reply")

        val receivedMessage = intent.getStringExtra("last_message")
        val contactName = intent.getStringExtra("contact_name")
        val contactIcon = intent.getIntExtra("contact_icon", 0)
        val chatId = intent.getIntExtra("chat_id", 0)


        // sending quick response
        CoroutineScope(Dispatchers.IO).launch {
            chatRepository.sendMessage(message.toString(), chatId.toLong())
        }

        val chatPartner = Person.Builder()
            .setName(contactName)
            .setIcon(IconCompat.createWithResource(context, contactIcon))
            .setImportant(true)
            .build()

        // Build a new notification, which informs the user that the system
        // handled their interaction with the previous notification.
        val repliedNotification = NotificationCompat.Builder(context, CHANNEL_NEW_MESSAGE)
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle(contactName)
            .setContentText("You: $message")
            .setStyle(
                NotificationCompat.MessagingStyle(chatPartner)
                    .addMessage(receivedMessage, System.currentTimeMillis(), chatPartner)
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

        NotificationManagerCompat.from(context).notify(chatId+10, repliedNotification)
    }
}
