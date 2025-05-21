package com.example.chatty.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.chatty.repository.ChatRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReadReceiver : BroadcastReceiver() {
    @Inject
    lateinit var chatRepository: ChatRepository

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReadReceiver", "Intent received")
        val messageID = intent.getLongExtra("message_id", 0)
        val chatId = intent.getIntExtra("chat_id", 0)

        CoroutineScope(Dispatchers.IO).launch {
            chatRepository.markAsRead(messageID.toLong())
        }

        NotificationManagerCompat.from(context).cancel(chatId)
    }
}