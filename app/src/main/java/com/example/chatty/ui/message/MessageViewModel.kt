package com.example.chatty.ui.message

import android.app.Application
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        Log.d("MessageViewModel", "onFragmentVisible")
        _isVisible = true
    }

    fun onFragmentHidden() {
        Log.d("MessageViewModel", "onFragmentHidden")
        _isVisible = false
    }

    init {
        notifications.setupChannel()
    }

    // TODO: change _chat.value!! with a check for null

    // changed from UI by clicking on contact
    private val _chatId = MutableLiveData(0L)

    // watch for changes in chatId to retrieve the chat
    val chat: LiveData<Chat> = _chatId.switchMap { newChatId ->
        chatRepository.getChat(newChatId)
    }

    // watch for changes in chatId to retrieve the chat messages
    @OptIn(ExperimentalCoroutinesApi::class)
    val chatMessages: LiveData<List<Message>> = _chatId.switchMap { newChatId ->
        chatRepository.getMessages(newChatId)
    }

    fun setChatId(chatId: Long) {
        _chatId.value = chatId
        markUnreadAsRead(chatId)
    }

    fun markUnreadAsRead(chatId: Long){
        viewModelScope.launch {
            chatRepository.markUnreadAsRead(chatId)
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(
                Message(
                    content = content,
                    timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                    chatId = _chatId.value!!,
                    isIncoming = false
                )
            )

            // more info for generating photos and other content at https://ai.google.dev/api/generate-content#text
            val generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash",
                apiKey = BuildConfig.API_KEY,
                systemInstruction = content {
                    text("Please respond to this conversation like the meme ${chat.value!!.name}.")
                }
            )

            val messageList = chatRepository.getMessages(chat.value!!.id).asFlow().first()
            val chatContents = messageList.map { Content.Builder().text(it.content).build() }.toList()

            val chatBot = generativeModel.startChat(chatContents)

            val response = try {
                withContext(Dispatchers.IO){
                    chatBot.sendMessage(content).text ?: "..."
                }
            } catch (e: Exception) {
                Log.d("MessageViewModel", "Generating reply")
                e.printStackTrace()
                e.message
            }

            kotlinx.coroutines.delay(1000)
            if (response != null) {
                val message = Message(
                    content = response,
                    timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                    chatId = _chatId.value!!,
                    isIncoming = true,
                    read = false
                )

                val messageId = chatRepository.sendMessage(message)
                message.id = messageId
                if (!_isVisible)
                    notifications.showNotification(message, chat.value!!)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MessageViewModel", "onCleared")
    }
}