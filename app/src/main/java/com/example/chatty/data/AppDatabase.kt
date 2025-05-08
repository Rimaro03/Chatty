package com.example.chatty.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chatty.models.Chat
import com.example.chatty.models.Contact
import com.example.chatty.models.Message

@Database(
    entities = [
        Contact::class,
        Message::class,
        Chat::class ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao
    abstract fun chatDao(): ChatDao
}