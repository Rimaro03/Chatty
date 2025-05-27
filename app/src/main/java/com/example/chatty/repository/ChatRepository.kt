package com.example.chatty.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import com.example.chatty.data.ChatDao
import com.example.chatty.data.MessageDao
import com.example.chatty.models.Chat
import com.example.chatty.models.ChatWithLastMessage
import com.example.chatty.models.Message
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
) {
    // chat
    fun getChats(): LiveData<List<Chat>> = chatDao.getAll()
    fun getChatsWithLastMessage(): LiveData<List<ChatWithLastMessage>> = chatDao.getChatsWithLastMessage()

    fun getChat(chatId: Long) = chatDao.getById(chatId)

    // messages
    fun getMessages(chatId: Long): LiveData<List<Message>> = messageDao.getAllByChatId(chatId)
    suspend fun clearChatHistory(chatId: Long) = messageDao.deleteAllByChatId(chatId)
    suspend fun clearHistory() = messageDao.deleteAll()
    suspend fun markAsRead(messageId: Long) = messageDao.markAsRead(messageId)

    suspend fun sendMessage(message: Message): Long {
        return messageDao.insert(message)
    }
}