package com.example.chatty.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chatty.models.Chat
import com.example.chatty.models.ChatWithLastMessage

@Dao
interface ChatDao {
    @Query("SELECT * FROM Chat")
    fun getAll(): LiveData<List<Chat>>

    @Query("SELECT * FROM Chat WHERE id = :chatId")
    fun getById(chatId: Long): LiveData<Chat>

    @Query("""
    SELECT 
        c.*, 
        (
            SELECT m.content
            FROM Message m 
            WHERE m.chatId = c.id 
            ORDER BY m.timestamp DESC 
            LIMIT 1
        ) AS messageContent,
        (
            SELECT m.read 
            FROM Message m 
            WHERE m.chatId = c.id 
            ORDER BY m.timestamp DESC 
            LIMIT 1
        ) AS messageRead
    FROM Chat c
""")
    fun getChatsWithLastMessage(): LiveData<List<ChatWithLastMessage>>

    @Insert
    suspend fun insert(vararg chat: Chat)
}

/*
    val id: Long = 0L,
    val content: String,
    val mediaUri: String? = null,
    val mediaMimeType: String? = null,
    val timestamp: String? = null,
    val chatId: Long,
    val isIncoming: Boolean,
    val read: Boolean? = false
 */