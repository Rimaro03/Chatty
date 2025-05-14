package com.example.chatty.ui.message

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.chatty.BuildConfig
import com.example.chatty.models.Chat
import com.example.chatty.models.Contact
import com.example.chatty.models.Message
import com.example.chatty.repository.ChatRepository
import com.example.chatty.utils.Notifications
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import android.util.Log
import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.first
import java.util.jar.Attributes

@HiltViewModel
class MessageViewModel @Inject constructor(
    application: Application,
    private val chatRepository: ChatRepository
): ViewModel() {
    // notifications set-up
    private var notifications: Notifications =
        Notifications(context = application.applicationContext)
    private var _isVisible: Boolean = false

    fun onFragmentVisible() {
        _isVisible = true
    }

    fun onFragmentHidden() {
        _isVisible = false
    }

    init {
        notifications.setupChannel()
    }

    // TODO: change _chat.value!! with a check for null

    // changed from UI by clicking on contact
    private val _contactId = MutableLiveData(0L)
    // watch for changes in contactId to retrieve the chat
    private val _chat: LiveData<Chat> = _contactId.switchMap { contactId ->
        chatRepository.getChat(contactId)
    }
    // watch for changes in contactId to retrieve the chat messages
    val chatMessages: LiveData<List<Message>> = _chat.switchMap {
        // chat MAY not exist in the DB
        chatRepository.getMessages(_chat.value!!.id)
    }

    fun setContactId(contactId: Long) {
        _contactId.value = contactId
    }

    fun sendMessage(content: String) {
        send(
            Message(
                content = content,
                senderId = 0L, // I am the sender
                timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                chatId = _chat.value!!.id
            )
        )

        // more info for generating photos and other content at https://ai.google.dev/api/generate-content#text
        val generativeModel = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = BuildConfig.API_KEY,
            systemInstruction = content {
                text("Please respond to this conversation like an old friend of mine that I'm texting with.")
            }
        )

        viewModelScope.launch {
            val chat = generativeModel.startChat()
            val response = try {
                chat.sendMessage(content).text ?: "..."
            } catch (e: Exception) {
                e.printStackTrace()
                e.message
            }

            if (response != null) {
                val message = Message(
                    content = response,
                    senderId = _contactId.value!!, // I am the sender
                    timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                    chatId = _chat.value!!.id
                )
                send(message)

                val name = chatRepository.getContactNameById(_contactId.value!!).asFlow().first()
                Log.d("MessageViewModel", "Contact name: $name")


                if (!_isVisible) {
                    notifications.showNotification(message, name)
                }
            }
        }
    }

    // receive the intent from the notification


    private fun send(message: Message) {
        viewModelScope.launch {
            chatRepository.sendMessage(message)
        }
    }

}