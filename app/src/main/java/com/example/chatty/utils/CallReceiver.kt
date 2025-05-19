package com.example.chatty.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "DECLINE_CALL" -> {
                Log.d("CallReceiver", "Declining call")
                val callIntent = Intent(context, FakeCallService::class.java)
                context.stopService(callIntent)
            }
            "ANSWER_CALL" -> {
                Log.d("CallReceiver", "Answering call")
                val callIntent = Intent(context, FakeCallService::class.java)
                context.stopService(callIntent)

                callIntent.action = "ONGOING_CALL"
                context.startService(callIntent)
            }
        }
    }
}