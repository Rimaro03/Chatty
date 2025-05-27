package com.example.chatty.repository

import androidx.lifecycle.LiveData
import com.example.chatty.data.ChatDao
import com.example.chatty.data.MessageDao
import com.example.chatty.models.Message
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
) {
    // contact
    fun getChats() = chatDao.getAll()

    // chat
    fun getChat(chatId: Long) = chatDao.getById(chatId)

    // messages
    fun getMessages(chatId: Long): LiveData<List<Message>> = messageDao.getAllByChatId(chatId)
    suspend fun clearChatHistory(chatId: Long) = messageDao.deleteAllByChatId(chatId)
    suspend fun clearHistory() = messageDao.deleteAll()
    suspend fun markAsRead(chatId: Long) = messageDao.markAsRead(chatId)

    suspend fun sendMessage(message: Message) {
        messageDao.insert(message)
    }
}