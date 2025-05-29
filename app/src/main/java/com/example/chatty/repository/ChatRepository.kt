package com.example.chatty.repository

import androidx.lifecycle.LiveData
import com.example.chatty.data.ChatDao
import com.example.chatty.data.MessageDao
import com.example.chatty.models.Chat
import com.example.chatty.models.ChatWithLastMessage
import com.example.chatty.models.Message
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
    suspend fun sendMessage(message: Message): Long = messageDao.insert(message)
    fun getMessages(chatId: Long): LiveData<List<Message>> = messageDao.getAllByChatId(chatId)
    suspend fun clearChatHistory(chatId: Long) = messageDao.deleteAllByChatId(chatId)
    suspend fun clearHistory() = messageDao.deleteAll()
    suspend fun markAsRead(messageId: Long) = messageDao.markAsRead(messageId)
    suspend fun markAllAsRead(chatId: Long) = messageDao.markAllAsRead(chatId)
}