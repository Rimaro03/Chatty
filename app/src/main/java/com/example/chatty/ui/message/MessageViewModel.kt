package com.example.chatty.ui.message

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.chatty.BuildConfig
import com.example.chatty.models.Chat
import com.example.chatty.models.Message
import com.example.chatty.repository.ChatRepository
import com.example.chatty.utils.Notifications
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

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
    private val _chatId = MutableLiveData(0L)
    // watch for changes in contactId to retrieve the chat
    private val _chat: LiveData<Chat> = _chatId.switchMap { contactId ->
        chatRepository.getChat(contactId)
    }
    // watch for changes in contactId to retrieve the chat messages
    val chatMessages: LiveData<List<Message>> = _chat.switchMap {
            chatRepository.getMessages(_chat.value!!.id)
    }

    fun setContactId(contactId: Long) {
        _chatId.value = contactId
    }

    fun sendMessage(content: String) {
        send(
            Message(
                content = content,
                timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                chatId = _chat.value!!.id,
                isIncoming = false
            )
        )

        // more info for generating photos and other content at https://ai.google.dev/api/generate-content#text
        val generativeModel = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = BuildConfig.API_KEY,
            systemInstruction = content {
                text("Please respond to this conversation like the meme ${_chat.value!!.name}.")
            }
        )

        viewModelScope.launch {
            val messageList = chatRepository.getMessages(_chat.value!!.id).asFlow().first()
            val chatContents = messageList.map { Content.Builder().text(it.content).build() }.toList()

            val chat = generativeModel.startChat(chatContents)
            val response = try {
                chat.sendMessage(content).text ?: "..."
            } catch (e: Exception) {
                e.printStackTrace()
                e.message
            }

            if (response != null) {
                val message = Message(
                    content = response,
                    timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                    chatId = _chat.value!!.id,
                    isIncoming = true
                )
                send(message)

                if (!_isVisible)
                    notifications.showNotification(message, _chat.value!!)
            }
        }
    }

    private fun send(message: Message) {
        viewModelScope.launch {
            chatRepository.sendMessage(message)
        }
    }

}