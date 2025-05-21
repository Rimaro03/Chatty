package com.example.chatty.ui.message

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.chatty.utils.FakeCallService
import com.example.chatty.models.Chat
import com.example.chatty.models.Message
import com.example.chatty.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val chatRepository: ChatRepository
): ViewModel() {

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

    // TODO: change _chat.value!! with a check for null
    fun sendMessage(content: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(content, _chatId.value!!)
        }
    }

    fun startFakeCall(context: Context) {
        val intent = Intent(context, FakeCallService::class.java)
        intent.putExtra("chatId", chat.value!!.id)
        intent.putExtra("chatName", chat.value!!.name)
        intent.putExtra("chatIcon", chat.value!!.icon)
        intent.action = "INCOMING_CALL"
        context.startForegroundService(intent)
    }

    fun onFragmentVisible() = chatRepository.onFragmentVisible()

    fun onFragmentHidden() = chatRepository.onFragmentHidden()
}