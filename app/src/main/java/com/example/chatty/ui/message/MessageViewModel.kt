package com.example.chatty.ui.message

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
import com.example.chatty.notifications.ImageNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val iconNotification: ImageNotification
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
        chatRepository.currentChatId = chatId
        markAllAsRead(chatId)
    }

    fun markAllAsRead(chatId: Long){
        viewModelScope.launch {
            chatRepository.markAllAsRead(chatId)
        }
    }

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

    fun showIconNotification(context: Context) {
        iconNotification.showFakeDownloadAndImageNotification(chat.value!!)
    }

    fun onFragmentHidden() = chatRepository.onFragmentHidden()

    // PLAYER
    fun playAudio() {
        chatRepository.play(chat.value!!)
    }
    fun stopAudio() = chatRepository.stop()

    override fun onCleared() {
        super.onCleared()
        chatRepository.releasePlayer()
    }
}