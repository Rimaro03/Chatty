package com.example.chatty.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chatty.model.Contact
import com.example.chatty.model.Message

@Database(
    entities = [
        Contact::class,
        Message::class ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao
}