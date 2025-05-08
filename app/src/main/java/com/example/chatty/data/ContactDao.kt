package com.example.chatty.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.chatty.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM Contact")
    fun getAll(): Flow<List<Contact>>

    @Insert
    fun insert(vararg contact: Contact)

    @Delete
    fun delete(vararg contact: Contact)
}