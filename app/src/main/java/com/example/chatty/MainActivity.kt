package com.example.chatty

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.data.Message
import android.os.Build
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {
    private val messageViewModel: MessageViewModel by viewModels{ MessageViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // message RecyclerView
        val messageRecyclerView = findViewById<RecyclerView>(R.id.message_rv_list)
        val messageAdapter = MessageAdapter(mutableListOf<Message>())
        messageRecyclerView.adapter = messageAdapter

        messageViewModel.messageList.observe(this) { newList ->
            messageRecyclerView.adapter = MessageAdapter(newList)
        }

        //input field
        val messageInput = findViewById<EditText>(R.id.message_input)
        val sendButton = findViewById<Button>(R.id.send_btn)

        sendButton.setOnClickListener {
            if(messageInput.text.toString().isEmpty()) return@setOnClickListener

            val message = messageInput.text.toString()
            messageViewModel.sendMessage(Message(message, false))
            messageInput.text.clear()
        }

        //notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel.
            val name = "New message"
            val descriptionText = "New message has arrived"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(1.toString(), name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        var builder = NotificationCompat.Builder(this, 1.toString())
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle("New message")
            .setContentText("New message")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }
}