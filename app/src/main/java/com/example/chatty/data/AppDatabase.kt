package com.example.chatty.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.chatty.R
import com.example.chatty.models.Chat
import com.example.chatty.models.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

@Database(
    entities = [
        Message::class,
        Chat::class ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun chatDao(): ChatDao
}

class DatabaseCallback(
    private val chatDaoProvider: Provider<ChatDao>
): RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Populate in background
        CoroutineScope(Dispatchers.IO).launch {
            chatDaoProvider.get().insert(Chat(
                name = "Boneca Ambalabu",
                icon = R.drawable.boneca,
                audio = "android.resource://com.example.chatty/${R.raw.boneca_ambalabu}"
            ))
            chatDaoProvider.get().insert(Chat(
                name = "Bombardino Crocodilo",
                icon = R.drawable.bombardino,
                audio = "android.resource://com.example.chatty/${R.raw.bombardino_crocodilo}"
            ))
        }
    }
}