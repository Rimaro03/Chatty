package com.example.chatty.data

import androidx.room.Dao
import androidx.room.Query
import com.example.chatty.models.Chat

@Dao
interface ChatDao {
    @Query("SELECT * FROM Chat")
    fun getAll(): List<Chat>

    @Query("SELECT * FROM Chat WHERE contactId = :contactId")
    fun getByContactId(contactId: Long): Chat?
}