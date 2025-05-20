package com.example.chatty.ui.message

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.chatty.BuildConfig
import com.example.chatty.utils.FakeCallService
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
        chatRepository.isFragmentVisible = true
    }

    fun onFragmentHidden() {
        _isVisible = false
        chatRepository.isFragmentVisible = false
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
    val chatMessages: LiveData<List<Message>> = _chatId.switchMap { newChatId ->
            chatRepository.getMessages(newChatId)
    }

    fun setChatId(chatId: Long) {
        _chatId.value = chatId
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(
                Message(
                    content = content,
                    timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                    chatId = chat.value!!.id,
                    isIncoming = false
                ),
                notifications
            )
        }
    }


    //private fun send(message: Message) {
    //    viewModelScope.launch {
    //        chatRepository.sendMessage(message)
    //    }
    //}

    fun startFakeCall(context: Context) {
        val intent = Intent(context, FakeCallService::class.java)
        intent.putExtra("chatId", chat.value!!.id)
        intent.putExtra("chatName", chat.value!!.name)
        intent.putExtra("chatIcon", chat.value!!.icon)
        intent.action = "INCOMING_CALL"
        context.startForegroundService(intent)
    }

}