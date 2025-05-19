package com.example.chatty.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.chatty.R

class OngoingCallActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ongoing_call)

        val iconView = findViewById<ImageView>(R.id.contact_icon)
        val nameView = findViewById<TextView>(R.id.contact_name)
        val hangupButton = findViewById<Button>(R.id.hangup_button)

        // Get data from intent (fallbacks for demo)
        val contactName = intent.getStringExtra("chatName") ?: "Caller Name"
        val contactIcon = intent.getIntExtra("chatIcon", R.drawable.boneca)

        iconView.setImageResource(contactIcon)
        nameView.text = contactName

        hangupButton.setOnClickListener {
            // Stop the service
            val callIntent = Intent(this, FakeCallService::class.java)
            stopService(callIntent)
            finish()
        }
    }
}