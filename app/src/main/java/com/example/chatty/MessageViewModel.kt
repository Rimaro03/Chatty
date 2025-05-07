package com.example.chatty

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chatty.data.Message
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class MessageViewModel(private val applicationContext: Context): ViewModel() {
    private val _messageList = MutableLiveData<List<Message>>(emptyList())
    val messageList: LiveData<List<Message>> get() = _messageList

    private var notificationHelper: NotificationHelper =
        NotificationHelper(context = applicationContext)

    init {
        notificationHelper.setupChannel()
    }

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
                text("Please respond to this conversation like an old friend of mine that I'm texring with.")
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
                addMessage(Message(content = response, isIncoming = true))
            }

            notificationHelper.showNotification(
                Message(
                    content = response ?: "",
                    isIncoming = true
                )
            )
        }
    }

    fun clearMessages() {
        _messageList.value = emptyList()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val applicationContext =
                    (this[ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY] as ChattyApplication).applicationContext
                MessageViewModel(
                    applicationContext = applicationContext
                )
            }
        }
    }

}