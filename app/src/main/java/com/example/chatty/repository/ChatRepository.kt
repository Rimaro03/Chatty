package com.example.chatty.repository

import com.example.chatty.data.ChatDao
import com.example.chatty.data.MessageDao
import com.example.chatty.models.Message
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
    suspend fun getMessages(chatId: Long) = messageDao.getAllByChatId(chatId)
    suspend fun clearChatHistory(chatId: Long) = messageDao.deleteAllByChatId(chatId)
    suspend fun clearHistory() = messageDao.deleteAll()

    suspend fun sendMessage(message: Message) {
        messageDao.insert(message)
    }
}