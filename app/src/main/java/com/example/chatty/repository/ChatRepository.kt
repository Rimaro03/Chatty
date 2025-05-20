package com.example.chatty.repository

import androidx.lifecycle.asFlow
import com.example.chatty.BuildConfig
import com.example.chatty.data.ChatDao
import com.example.chatty.data.MessageDao
import com.example.chatty.models.Message
import com.example.chatty.utils.Notifications
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import android.util.Log
import androidx.lifecycle.LiveData

class ChatRepository @Inject constructor(
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
) {
    var isFragmentVisible = false

    // contact
    fun getChats() = chatDao.getAll()

    // chat
    fun getChat(chatId: Long) = chatDao.getById(chatId)

    // messages
    fun getMessages(chatId: Long) = messageDao.getAllByChatId(chatId)
    suspend fun clearChatHistory(chatId: Long) = messageDao.deleteAllByChatId(chatId)
    suspend fun clearHistory() = messageDao.deleteAll()

    suspend fun sendMessage(message: Message, notification: Notifications) {
        messageDao.insert(message)

        val chat = getChat(message.chatId).asFlow().first()

        Log.d ("ChatRepository", "Message sent: ${message.content}")
        Log.d ("ChatRepository", "Chat ${chat.name} is visible: $isFragmentVisible")

        val generativeModel = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = BuildConfig.API_KEY,
            systemInstruction = content {
                text("Please respond to this conversation like the meme ${chat.name}.")
            }
        )

        val messageList = getMessages(message.chatId).asFlow().first()
        val chatContents = messageList.map { Content.Builder().text(it.content).build() }.toList()

        val chatBot = generativeModel.startChat(chatContents)
        val response = try {
            chatBot.sendMessage(message.content).text ?: "..."
        } catch (e: Exception) {
            e.printStackTrace()
            e.message
        }

        if (response != null) {
            val message = Message(
                content = response,
                timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                chatId = message.chatId,
                isIncoming = true
            )
            Thread.sleep(1000)

            messageDao.insert(message)
            Log.d ("ChatRepository", "Message received: ${message.content}")
            if (!isFragmentVisible) {
                notification.showNotification(message, chat)
            }
        }
    }
}