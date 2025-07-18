package com.example.chatty.repository

import androidx.lifecycle.asFlow
import com.example.chatty.BuildConfig
import com.example.chatty.data.ChatDao
import com.example.chatty.data.MessageDao
import com.example.chatty.models.Chat
import com.example.chatty.models.ChatWithLastMessage
import com.example.chatty.models.Message
import com.example.chatty.notifications.Notifications
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.chatty.utils.PlaybackManager

class ChatRepository @Inject constructor(
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
    private val notifications: Notifications,
    private val playbackManager: PlaybackManager
) {
    // chat
    var currentChatId: Long = 0L

    init {
        notifications.setupChannel()
        currentChatId = 0L
    }

    fun onFragmentHidden() {
        currentChatId = 0L
    }

    // chat
    fun getChats(): LiveData<List<Chat>> = chatDao.getAll()
    fun getChatsWithLastMessage(): LiveData<List<ChatWithLastMessage>> = chatDao.getChatsWithLastMessage()
    fun getChat(chatId: Long) = chatDao.getById(chatId)

    // messages
    fun getMessages(chatId: Long) = messageDao.getAllByChatId(chatId)
    suspend fun clearHistory() = messageDao.deleteAll()

    suspend fun sendMessage(content: String, chatId: Long) {
        val userMessage = Message(
            content = content,
            timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
            chatId = chatId,
            isIncoming = false
        )

        messageDao.insert(userMessage)

        val chat = getChat(userMessage.chatId).asFlow().first()

        val generativeModel = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = BuildConfig.API_KEY,
            systemInstruction = content {
                text("Please respond to this conversation like the meme ${chat.name}.")
            }
        )

        val messageList = getMessages(userMessage.chatId).asFlow().first()
        val chatContents = messageList.map { Content.Builder().text(it.content).build() }.toList()

        val chatBot = generativeModel.startChat(chatContents)
        val response = try {
            chatBot.sendMessage(userMessage.content).text ?: "..."
        } catch (e: Exception) {
            e.printStackTrace()
            e.message
        }

        kotlinx.coroutines.delay(2000)
        if (response != null) {
            val botMessage = Message(
                content = response,
                timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                chatId = userMessage.chatId,
                isIncoming = true,
                read = false
            )

            val messageId = messageDao.insert(botMessage)
            botMessage.id = messageId
            Log.d ("ChatRepository", "Message received: ${botMessage.content}")
            if(botMessage.chatId == currentChatId) {
                messageDao.markAsRead(messageId)
            }
            else {
                notifications.showNotification(botMessage, chat)
                //notifications.showBubbleNotification(message, chat)
            }
        }
    }
    suspend fun markAsRead(messageId: Long) = messageDao.markAsRead(messageId)
    suspend fun markAllAsRead(chatId: Long) = messageDao.markAllAsRead(chatId)

    // PLAYER
    fun play(chat: Chat) {
        playbackManager.startPlayback(chat)
    }

    fun stop() {
        playbackManager.stopPlayback()
    }

    fun releasePlayer() {
        playbackManager.release()
        Log.d("ChatRepository", "Player released")
    }
}