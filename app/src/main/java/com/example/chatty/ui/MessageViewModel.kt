package com.example.chatty.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatty.BuildConfig
import com.example.chatty.model.Message
import com.example.chatty.repository.ChatRepository
import com.example.chatty.utils.Notifications
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    application: Application,
    chatRepository: ChatRepository
): ViewModel() {
    private val _messageList = MutableLiveData<List<Message>>(emptyList<Message>())
    val messageList: LiveData<List<Message>> get() = _messageList

    private var notifications: Notifications =
        Notifications(context = application.applicationContext)


    fun addMessage(message: Message) {
        val currentList = _messageList.value.orEmpty()
        val updatedList = currentList + message
        _messageList.value = updatedList
    }

    fun sendMessage(message: Message) {
        addMessage(message)

        val generativeModel = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = BuildConfig.API_KEY,
            systemInstruction = content {
                text("Please respond to this conversation like an old friend of mine that I'm texting with.")
            }
        )

        viewModelScope.launch {
            val chat = generativeModel.startChat()
            var response = try {
                chat.sendMessage(message.content).text ?: "..."
            } catch (e: Exception) {
                e.printStackTrace()
                e.message
            }

            if (response != null) {
                addMessage(
                    Message(
                        id = 1L,
                        contactId = 2L,
                        content = response
                    )
                )
            }

            notifications.showNotification(
                Message(
                    id = 1L,
                    contactId = 2L,
                    content = response ?: ""
                )
            )
        }
    }

    fun clearMessages() {
        _messageList.value = emptyList<Message>()
    }

}