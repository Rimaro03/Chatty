package com.example.chatty
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.data.Message

class MainActivity : AppCompatActivity() {
    private val messageViewModel: MessageViewModel by viewModels{ MessageViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

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

        // request notifications permission
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    // not necessary to kill the app... but for now it's fine
                    this.finishAffinity()
                }
            }

        // TODO: the following method is available only from API level 33, do somethink if 30 <= apiLevel <= 32
        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }
}