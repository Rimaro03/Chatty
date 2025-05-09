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
    private val myChatId: Long = 0L

    // contact
    fun getContacts() = contactDao.getAll()

    // chat
    fun getChat(chatId: Long) = chatDao.getById(chatId)

    // messages
    fun getMessages(chatId: Long) = messageDao.getAllByChatId(chatId)
    fun sendMessage(message: Message) = messageDao.insert(message)

    fun sendMessage(message: String, contactId: Long) {
        /*
        * Insert message in DB
        * generate repsonse
        * insert response in DB
        * generate notification*/
    }
}