package com.example.chatty.utils

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.Person
import com.example.chatty.R
import com.example.chatty.notifications.CallNotification
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FakeCallService: Service() {
    @Inject
    lateinit var callNotification: CallNotification
    // Person that represents the caller
    private lateinit var person: Person
    // id of the chat
    private var id : Long = 0L

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // take the chat from the intent
        id = intent?.getLongExtra("chatId", 0L)!!

        person = Person.Builder()
            .setName(intent.getStringExtra("chatName") ?: "Caller Name")
            .setIcon(androidx.core.graphics.drawable.IconCompat.createWithResource(appContext, intent.getIntExtra("chatIcon", R.drawable.boneca)))
            .build()

        when (intent.action) {
            "INCOMING_CALL" -> {
                // i set the id to 1 + chat.id to avoid conflict with the notification id
                startForeground(4, callNotification.createIncomingNotification(id, person))
            }
            "ONGOING_CALL" -> {
                // i set the id to 1 + chat.id to avoid conflict with the notification id
                startForeground(4, callNotification.createOngoingNotification(person))
            }
        }

        return START_NOT_STICKY
    }

    private val appContext by lazy {
        applicationContext
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent("CALL_ENDED")
        sendBroadcast(intent)
    }

}