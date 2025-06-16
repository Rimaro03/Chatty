package com.example.chatty.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.chatty.R

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "DECLINE_CALL" -> {
                val callIntent = Intent(context, FakeCallService::class.java)
                context.stopService(callIntent)
            }
            "ANSWER_CALL" -> {
                val callIntent = Intent(context, FakeCallService::class.java)
                context.stopService(callIntent)

                callIntent.action = "ONGOING_CALL"
                callIntent.putExtra("chatId", intent.getLongExtra("chatId", 0L))
                callIntent.putExtra("chatName", intent.getStringExtra("chatName"))
                callIntent.putExtra("chatIcon", intent.getIntExtra("chatIcon", R.drawable.boneca))
                context.startService(callIntent)
            }
        }
    }
}