package com.example.chatty.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chatty.models.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM Message")
    fun getAll(): List<Message>

    @Query("SELECT * FROM Message WHERE senderId = :senderId")
    fun getBySenderId(senderId: Long): Flow<List<Message>>

    @Insert
    fun insert(message: Message)

    @Query("DELETE FROM Message")
    fun deleteAll()
}