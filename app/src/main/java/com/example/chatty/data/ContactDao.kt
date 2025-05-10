package com.example.chatty.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.chatty.models.Contact

@Dao
interface ContactDao {
    @Query("SELECT * FROM Contact")
    fun getAll(): LiveData<List<Contact>>

    @Insert
    fun insert(vararg contact: Contact)

    @Delete
    fun delete(vararg contact: Contact)
}