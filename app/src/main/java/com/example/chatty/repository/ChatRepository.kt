package com.example.chatty.repository

import com.example.chatty.data.ChatDao
import com.example.chatty.utils.Notifications
import com.example.chatty.data.ContactDao
import com.example.chatty.data.MessageDao
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val contactDao: ContactDao,
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
    private val notifications: Notifications
) {
    private val myChatId: Long = 0L

    init {
        notifications.setupChannel()
    }

    fun getContacts() = contactDao.getAll()



    fun sendMessage(message: String, contactId: Long) {
        /*
        * Insert message in DB
        * generate repsonse
        * insert response in DB
        * generate notification*/
    }
}