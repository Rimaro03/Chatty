package com.example.chatty.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat


class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val callIntent = Intent(context, FakeCallService::class.java)
        context.stopService(callIntent)
    }
}