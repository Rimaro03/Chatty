package com.example.chatty.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chatty.models.Message

@Dao
interface MessageDao {
    @Query("SELECT * FROM Message WHERE chatId = :chatId")
    fun getAllByChatId(chatId: Long): LiveData<List<Message>>

    @Insert
    fun insert(message: Message)

    @Query("DELETE FROM Message WHERE id = :chatId")
    fun deleteAllByChatId(chatId: Long)
}