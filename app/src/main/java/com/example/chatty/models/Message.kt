package com.example.chatty.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["senderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Chat::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Message (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val content: String,
    val mediaUri: String? = null,
    val mediaMimeType: String? = null,
    val timestamp: String? = null,
    val senderId: Long,
    val chatId: Long
) {
    // contact with 0L is me
    val isIncoming: Boolean
        get() = senderId != 0L
}