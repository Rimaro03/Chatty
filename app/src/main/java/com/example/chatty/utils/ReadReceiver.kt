package com.example.chatty.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ReadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        Log.d("ReadReceiver", "Intent received")
    }
}