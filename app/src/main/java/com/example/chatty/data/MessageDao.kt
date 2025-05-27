package com.example.chatty.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chatty.models.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM Message WHERE chatId = :chatId")
    fun getAllByChatId(chatId: Long): LiveData<List<Message>>

    @Query("SELECT * FROM Message WHERE id = :id")
    fun getById(id: Long): LiveData<Message>

    @Query("SELECT * FROM Message WHERE chatId = :chatId ORDER BY timestamp DESC LIMIT 1")
    fun getLastedMessage(chatId: Long): LiveData<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: Message): Long

    @Query("DELETE FROM Message WHERE id = :chatId")
    suspend fun deleteAllByChatId(chatId: Long)

    @Query("UPDATE Message SET read = 1 WHERE id = :messageId")
    suspend fun markAsRead(messageId: Long)

    @Query("DELETE FROM Message")
    suspend fun deleteAll()
}