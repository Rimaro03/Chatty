package com.example.chatty.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.chatty.models.Chat
import com.example.chatty.models.Contact
import com.example.chatty.models.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

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

class DatabaseCallback(
    private val contactDaoProvider: Provider<ContactDao>,
    private val chatDaoProvider: Provider<ChatDao>
): RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Populate in background
        CoroutineScope(Dispatchers.IO).launch {
            contactDaoProvider.get().insert(Contact(0L, "You", ""))
            contactDaoProvider.get().insert(Contact(1L, "Boneca Ambalabu", ""))
            chatDaoProvider.get().insert(Chat(1L, 1L))
        }
    }
}