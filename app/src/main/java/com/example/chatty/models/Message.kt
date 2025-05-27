package com.example.chatty.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
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
    var id: Long = 0,
    val content: String,
    val mediaUri: String? = null,
    val mediaMimeType: String? = null,
    val timestamp: String? = null,
    val chatId: Long,
    val isIncoming: Boolean,
    val read: Boolean? = true
)