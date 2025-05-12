package com.example.chatty.repository

import com.example.chatty.data.ChatDao
import com.example.chatty.data.ContactDao
import com.example.chatty.data.MessageDao
import com.example.chatty.models.Message
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val contactDao: ContactDao,
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
) {
    // contact
    fun getContacts() = contactDao.getAll()

    // chat
    fun getChat(chatId: Long) = chatDao.getById(chatId)

    // messages
    fun getMessages(chatId: Long) = messageDao.getAllByChatId(chatId)

    suspend fun sendMessage(message: Message) {
        messageDao.insert(message)
    }
}