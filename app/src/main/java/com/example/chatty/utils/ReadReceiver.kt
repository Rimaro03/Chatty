package com.example.chatty.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class ReadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReadReceiver", "Intent received")

        val chatId = intent.getIntExtra("chat_id", 0)

        NotificationManagerCompat.from(context).cancel(chatId)
    }
}