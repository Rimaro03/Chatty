package com.example.chatty.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.example.chatty.models.Contact

@Dao
interface ContactDao {
    @Query("SELECT * FROM Contact")
    fun getAll(): List<Contact>

    @Delete
    fun delete(vararg contact: Contact)
}