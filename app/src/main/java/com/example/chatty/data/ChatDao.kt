package com.example.chatty.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chatty.models.Chat

@Dao
interface ChatDao {
    @Query("SELECT * FROM Chat")
    fun getAll(): LiveData<List<Chat>>

    @Query("SELECT * FROM Chat WHERE id = :chatId")
    fun getById(chatId: Long): LiveData<Chat>

    @Query("SELECT * FROM Chat WHERE contactId = :contactId")
    fun getByContactId(contactId: Long): LiveData<Chat>

    @Insert
    fun insert(vararg chat: Chat)
}